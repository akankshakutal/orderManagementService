package com.orderManagement.orderManagementService.order

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
class OrderController(val orderService: OrderService, val orderValidator: OrderValidator) {

    @PostMapping("/create/order")
    fun createOffer(@RequestBody orderDetails: OrderDetails): Mono<OrderResponse> {
        val validatedOrderDetails = orderValidator.validate(orderDetails)
        return orderService.order(validatedOrderDetails)
    }

    @GetMapping("/get/order")
    fun getOffer(): Flux<OrderResponse> {
        return orderService.getOrder()
    }
}