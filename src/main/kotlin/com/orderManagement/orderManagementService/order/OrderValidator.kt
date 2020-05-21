package com.orderManagement.orderManagementService.order

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OrderValidator {
    fun validate(orderDetails: OrderDetails): Mono<OrderDetails> {
        if (orderDetails.quantity < 1) {
            throw InvalidQuantityException()
        }
        return Mono.just(orderDetails)
    }
}
