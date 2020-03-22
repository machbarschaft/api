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
        return firestoreClient.saveOrder(orderDetails = orderCreationDto.asMap(userId = userId, status = "to_be_delivered"), items = orderCreationDto.items)
    }

    fun findOrder(orderId: String): FirestoreOrder {
        return firestoreClient.findOrder(orderId = orderId)
    }

    fun findOrdersByUserId(userId: String): List<FirestoreOrder> {
        return firestoreClient.findOrdersByUserId(userId = userId)
    }

    fun updateItemStatus(userId: String, orderId: String, itemId: String, status: String) {
        // TODO check if user is allowed to see this document
        return firestoreClient.updateItemStatus(userId = userId, orderId = orderId, itemId = itemId, status = status)
    }

    fun updateOrderStatus(userId: String, orderId: String, status: String) {
        // TODO check if user is allowed to see this document
        return firestoreClient.updateOrderStatus(userId = userId, orderId = orderId, status = status)
    }

    fun deleteItem(userId: String, orderId: String, itemId: String) {
        // TODO check if user is allowed to see this document
        return firestoreClient.deleteItem(userId = userId, orderId = orderId, itemId = itemId)
    }

    fun findOrders(orderIds: List<String>): List<FirestoreOrder> {
        return firestoreClient.findOrders(orderIds = orderIds)
    }

    fun acceptOrder(userId: String, orderId: String): FirestoreUser {
        return firestoreClient.acceptOrder(acceptorId = userId, orderId = orderId)
    }

    fun declideOrder(userId: String, orderId: String) {
        return firestoreClient.declideOrder(decliderId = userId, orderId = orderId)
    }
}
