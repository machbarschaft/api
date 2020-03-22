package app.colivery.api.api

import app.colivery.api.FirestoreOrder
import app.colivery.api.OrderCreationDto
import app.colivery.api.UnauthorizedException
import app.colivery.api.config.SecurityUtils
import app.colivery.api.service.OrderService
import javax.validation.Valid
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/order")
class OrderResources(
    private val securityUtils: SecurityUtils,
    private val orderService: OrderService
) {

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun createOrder(@Valid @RequestBody orderCreationDto: OrderCreationDto): FirestoreOrder {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.createOrder(userId = uid, orderCreationDto = orderCreationDto)
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getOrder(@RequestParam(name = "order_id") orderId: String): FirestoreOrder {
        securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.findOrder(orderId = orderId)
    }

    @GetMapping("/own", produces = [APPLICATION_JSON_VALUE])
    fun findOwnOrders(): List<FirestoreOrder> {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.findOrdersByUserId(userId = uid)
    }

    @PostMapping("/update_order_status")
    fun updateItemStatus(@RequestParam(name = "order_id") orderId: String, @RequestParam(name = "status") status: String) {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.updateOrderStatus(userId = uid, orderId = orderId, status = status)
    }

    @PostMapping("/update_item_status")
    fun updateItemStatus(@RequestParam(name = "order_id") orderId: String, @RequestParam(name = "item_id") itemId: String, @RequestParam(name = "status") status: String) {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.updateItemStatus(userId = uid, orderId = orderId, itemId = itemId, status = status)
    }

    @DeleteMapping("/delete_item")
    fun deleteItem(@RequestParam(name = "order_id") orderId: String, @RequestParam(name = "item_id") itemId: String) {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.deleteItem(userId = uid, orderId = orderId, itemId = itemId)
    }
}
