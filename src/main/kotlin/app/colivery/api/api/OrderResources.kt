package app.colivery.api.api

import app.colivery.api.*
import app.colivery.api.config.SecurityUtils
import app.colivery.api.service.OrderService
import app.colivery.api.service.UserService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/order")
class OrderResources(
    private val securityUtils: SecurityUtils,
    private val orderService: OrderService,
    private val userService: UserService
) {

    @PostMapping(consumes = [APPLICATION_JSON_VALUE])
    fun createOrder(@Valid @RequestBody orderCreationDto: OrderCreationDto): FirestoreOrder {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        val user = userService.findUser(uid)

        return orderService.createOrder(userId = uid, orderCreationDto = orderCreationDto, dropoffAddress = user.address)
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getOrder(@RequestParam(name = "order_id") orderId: String): FirestoreOrder {
        securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.findOrder(orderId = orderId)
    }

    @GetMapping("/batch", produces = [APPLICATION_JSON_VALUE])
    fun getOrders(@RequestParam(name = "order_ids") orderIds: String): List<FirestoreOrder> {
        securityUtils.principal
            ?: throw UnauthorizedException()

        val ids = orderIds.split(",")

        return orderService.findOrders(orderIds = ids)
    }

    @GetMapping("/driver/own", produces = [APPLICATION_JSON_VALUE])
    fun findOwnDriverOrders(): List<OwnOrderDao> {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.findOrdersByDriverId(driverId = uid)
            .map {
                OwnOrderDao(it, userService.findUser(it.userId), it.driverUserId?.let { it1 -> userService.findUser(it1) })
            }
    }

    @GetMapping("/own", produces = [APPLICATION_JSON_VALUE])
    fun findOwnOrders(): List<OwnOrderDao> {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.findOrdersByUserId(userId = uid)
            .map {
                OwnOrderDao(it, userService.findUser(it.userId), it.driverUserId?.let { it1 -> userService.findUser(it1) })
            }
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

    @PostMapping("/accept")
    fun acceptOrder(@RequestParam(name = "order_id") orderId: String): FirestoreUser {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.acceptOrder(userId = uid, orderId = orderId)
    }

    @PostMapping("/declide")
    fun declideOrder(@RequestParam(name = "order_id") orderId: String) {
        val (uid) = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.declideOrder(userId = uid, orderId = orderId)
    }
}
