package com.orderManagement.orderManagementService.order

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class OrderController(val orderService: OrderService, val orderValidator: OrderValidator) {

    @PostMapping("/create/order")
    fun getCreditCardOffer(@RequestBody orderDetails: OrderDetails): Mono<OrderResponse> {
        val validatedOrderDetails = orderValidator.validate(orderDetails)
        return orderService.order(validatedOrderDetails)
    }
}