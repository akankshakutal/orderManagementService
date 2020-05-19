package com.orderManagement.orderManagementService

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrderController(val orderService: OrderService) {

    @PostMapping("/create/order")
    fun getCreditCardOffer(@RequestBody orderDetails: OrderDetails) {
        orderService.order(orderDetails)
    }
}