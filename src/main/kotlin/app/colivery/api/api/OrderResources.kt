package app.colivery.api.api

import app.colivery.api.FirestoreOrder
import app.colivery.api.OrderCreationDto
import app.colivery.api.UnauthorizedException
import app.colivery.api.config.AuthContext
import app.colivery.api.config.SecurityUtils
import app.colivery.api.service.OrderService
import javax.validation.Valid
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
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
        val authContext = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.createOrder(userId = authContext.uid, orderCreationDto = orderCreationDto)
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun getOrder(@RequestParam(name = "order_id") orderId: String): FirestoreOrder {
        val authContext = securityUtils.principal
            ?: throw UnauthorizedException()

        return orderService.findOrder(orderId = orderId)
    }

//    @GetMapping(produces = [APPLICATION_JSON_VALUE])
//    fun getUser(@RequestParam(name = "user_id") userId: String): FirestoreOrder {
//        return orderService.findOrder()
//    }
}
