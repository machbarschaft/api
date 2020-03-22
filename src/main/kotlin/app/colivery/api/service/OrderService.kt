package app.colivery.api.service

import app.colivery.api.FirestoreOrder
import app.colivery.api.FirestoreUser
import app.colivery.api.OrderCreationDto
import app.colivery.api.asMap
import app.colivery.api.client.FirestoreClient
import org.springframework.stereotype.Service

@Service
class OrderService(private val firestoreClient: FirestoreClient) {

    fun createOrder(userId: String, orderCreationDto: OrderCreationDto): FirestoreOrder {
        return firestoreClient.saveOrder(orderDetails = orderCreationDto.asMap(userId = userId), items = orderCreationDto.items)
    }

    fun findOrder(orderId: String): FirestoreOrder {
        return firestoreClient.findOrder(orderId = orderId)
    }

}
