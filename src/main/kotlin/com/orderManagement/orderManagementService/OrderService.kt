package com.orderManagement.orderManagementService

import com.orderManagement.orderManagementService.prospect.Prospect
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class OrderService(val prospectRepository: ProspectRepository) {
    fun order(orderDetails: OrderDetails): Mono<OrderResponse> {
        val itemName = orderDetails.itemName
        val quantity = orderDetails.quantity
        val paymentMode = orderDetails.paymentMode
        val email = orderDetails.email

        return prospectRepository.save(Prospect(itemName, quantity, paymentMode, email, "PLACED"))
                .map { OrderResponse(it.itemName, it.quantity, it.paymentMode, it.email, "PLACED") }
    }
}
