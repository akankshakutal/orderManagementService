package com.orderManagement.orderManagementService.prospect

import com.orderManagement.orderManagementService.order.PaymentMode
import com.orderManagement.orderManagementService.order.Status
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "orderDetails")
data class Prospect(val itemName: String, val quantity: Int, val cost: Int, val paymentMode: PaymentMode, val email: String, var status: Status) {
    @Id
    lateinit var id: String
}