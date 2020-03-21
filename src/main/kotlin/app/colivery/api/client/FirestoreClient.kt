package app.colivery.api.client

import app.colivery.api.FirestoreUser
import app.colivery.api.config.ORDER_COLLECTION_NAME
import app.colivery.api.config.ORDER_ITEM_COLLECTION_NAME
import app.colivery.api.config.USER_COLLECTION_NAME
import app.colivery.api.toUser
import com.google.cloud.firestore.Firestore
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service

@Service
@Primary
class FirestoreClient(firestore: Firestore) {

    private val userCollection = firestore.collection(USER_COLLECTION_NAME)
    private val orderCollection = firestore.collection(ORDER_COLLECTION_NAME)
    private val orderItemCollection = firestore.collection(ORDER_ITEM_COLLECTION_NAME)

    fun saveUser(userId: String, userDetails: Map<String, Any>) {
        userCollection.document(userId).set(userDetails).get()
    }

    fun findUser(userId: String): FirestoreUser {
        return userCollection.document(userId).toUser()
    }
}
