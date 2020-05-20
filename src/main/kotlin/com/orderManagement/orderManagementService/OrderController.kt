package com.orderManagement.orderManagementService

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class OrderController(val orderService: OrderService) {

    @PostMapping("/create/order")
    fun getCreditCardOffer(@RequestBody orderDetails: OrderDetails): Mono<OrderResponse> {
        return orderService.order(orderDetails)
    }
}