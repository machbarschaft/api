package app.colivery.api.client

import app.colivery.api.FirestoreOrder
import app.colivery.api.FirestoreUser
import app.colivery.api.OrderItemCreationDto
import app.colivery.api.asMap
import app.colivery.api.config.ORDER_COLLECTION_NAME
import app.colivery.api.config.USER_COLLECTION_NAME
import app.colivery.api.toOrder
import app.colivery.api.toOrderItem
import app.colivery.api.toUser
import com.google.cloud.firestore.Firestore
import com.google.cloud.firestore.SetOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class FirestoreClient(private val firestore: Firestore) {

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
        val productCollection = orderCollection.document(orderId).collection("items")
        items.forEach { productCollection.add(it.asMap()).get().get() }
        return findOrder(orderId = orderId)
    }

    fun findOrder(orderId: String): FirestoreOrder {
        logger.info("Fetching order $orderId")
        val items = orderCollection.document(orderId).collection("items").listDocuments().map { it.toOrderItem() }
        val order = orderCollection.document(orderId).toOrder(items = items)
        logger.info("result=$order")
        return order
    }

    fun findOrdersByUserId(userId: String): List<FirestoreOrder> {
        return orderCollection.whereEqualTo("user_id", userId).get().get().documents.map { orderSnapshot ->
            val items = orderCollection.document(orderSnapshot.id).collection("items").listDocuments().map { it.toOrderItem() }
            orderSnapshot.toOrder(items)
        }
    }

    fun updateItemStatus(userId: String?, orderId: String, itemId: String, status: String) {
        orderCollection.document(orderId).collection("items").document(itemId).set(mapOf("status" to status), SetOptions.merge()).get()
    }
}
