package com.orderManagement.orderManagementService.order

import com.orderManagement.orderManagementService.prospect.Prospect
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OrderService(val prospectRepository: ProspectRepository) {
    fun order(orderDetails: Mono<OrderDetails>): Mono<OrderResponse> {
        return orderDetails.flatMap {
            val itemName = it.itemName
            val quantity = it.quantity
            val paymentMode = it.paymentMode
            val email = it.email
            prospectRepository.save(Prospect(itemName, quantity, paymentMode.name, email, "PLACED"))
        }.map {
            OrderResponse(it.itemName, it.quantity, PaymentMode.valueOf(it.paymentMode), it.email, "PLACED")
        }
    }
}
