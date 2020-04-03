package app.colivery.api.client

import app.colivery.api.*
import app.colivery.api.config.ORDER_COLLECTION_NAME
import app.colivery.api.config.ORDER_ITEM_COLLECTION_NAME
import app.colivery.api.config.USER_COLLECTION_NAME
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import com.google.firebase.auth.FirebaseAuth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class FirestoreClient(private val firestore: Firestore,
                      private val firebaseAuth: FirebaseAuth) {

    private val logger: Logger = LoggerFactory.getLogger(FirestoreClient::class.java)

    private val userCollection
        get() = firestore.collection(USER_COLLECTION_NAME)
    private val orderCollection
        get() = firestore.collection(ORDER_COLLECTION_NAME)

    fun saveUser(userId: String, userDetails: Map<String, Any>) {
        logger.info("Saving user $userId - $userDetails")
        userCollection.document(userId).set(userDetails).get()
    }

    fun findUser(userId: String): FirestoreUser {
        logger.info("Fetching user $userId")
        return userCollection.document(userId).toUser()
    }

    fun saveOrder(orderDetails: Map<String, Any?>, items: List<OrderItemCreationDto>): FirestoreOrder {
        logger.info("Saving order - $orderDetails")
        val orderId = orderCollection.add(orderDetails).get().id
        val productCollection = orderCollection.document(orderId).collection(ORDER_ITEM_COLLECTION_NAME)
        items.forEach { productCollection.add(it.asMap()).get().get() }
        return findOrder(orderId = orderId)
    }

    fun findOrder(orderId: String): FirestoreOrder {
        logger.info("Fetching order $orderId")
        val items = orderCollection.document(orderId).collection(ORDER_ITEM_COLLECTION_NAME).listDocuments().map { it.toOrderItem() }
        val order = orderCollection.document(orderId).toOrder(items = items)
        logger.info("result=$order")
        return order
    }

    fun findOrdersByUserId(userId: String): List<FirestoreOrder> {
        return orderCollection.whereEqualTo("user_id", userId).get().get().documents.map { orderSnapshot ->
            val items = orderCollection.document(orderSnapshot.id).collection(ORDER_ITEM_COLLECTION_NAME).listDocuments().map { it.toOrderItem() }
            orderSnapshot.toOrder(items)
        }
    }

    fun findOrdersByDriverId(driverId: String): List<FirestoreOrder> {
        return orderCollection.whereEqualTo("driver_user_id", driverId).get().get().documents.map { orderSnapshot ->
            val items = orderCollection.document(orderSnapshot.id).collection(ORDER_ITEM_COLLECTION_NAME).listDocuments().map { it.toOrderItem() }
            orderSnapshot.toOrder(items)
        }
    }

    fun updateItemStatus(userId: String?, orderId: String, itemId: String, status: String) {
        orderCollection.document(orderId).collection(ORDER_ITEM_COLLECTION_NAME).document(itemId).set(mapOf("status" to status), SetOptions.merge()).get()
    }

    fun deleteItem(userId: String, orderId: String, itemId: String) {
        orderCollection.document(orderId).collection(ORDER_ITEM_COLLECTION_NAME).document(itemId).delete().get()
    }

    fun updateOrderStatus(userId: String, orderId: String, status: String) {
        orderCollection.document(orderId).set(mapOf("status" to status), SetOptions.merge()).get()
    }

    fun findOrders(orderIds: List<String>): List<FirestoreOrder> {
        val documents = orderIds.map { orderCollection.document(it) }.toTypedArray()
        return firestore.getAll(*documents).get().map { orderSnapshot ->
            val items = orderCollection.document(orderSnapshot.id).collection(ORDER_ITEM_COLLECTION_NAME).listDocuments().map { it.toOrderItem() }
            orderSnapshot.toOrder(items)
        }
    }

    fun acceptOrder(acceptorId: String, orderId: String): FirestoreUser {
        orderCollection.document(orderId).set(mapOf("driver_user_id" to acceptorId, "status" to "accepted"), SetOptions.merge())
        val userId = orderCollection.document(orderId).get().get().getString("user_id")
            ?: throw InternalServerException("Order found with unknown user. This should not happen.")
        return findUser(userId = userId)
    }

    fun declideOrder(decliderId: String, orderId: String) {
        val firestoreOrder = findOrder(orderId = orderId)
        if (firestoreOrder.driverUserId != decliderId) {
            throw BadRequestException("The driver is another person")
        }

        orderCollection.document(orderId).set(mapOf(
            "driver_user_id" to null,
            "status" to "to_be_delivered"
        ), SetOptions.merge())
    }

    fun deleteUser(userId: String) {
        this.orderCollection
            .whereEqualTo("user_id", userId)
            .get()
            .get()
            .documents
            .forEach {
                val orderId = it.id
                this.orderCollection.document(orderId)
                    .set(mapOf("status" to "consumer_canceled"), SetOptions.merge())
            }

        this.userCollection
            .document(userId)
            .delete()

        this.firebaseAuth.deleteUser(userId)
    }
}
