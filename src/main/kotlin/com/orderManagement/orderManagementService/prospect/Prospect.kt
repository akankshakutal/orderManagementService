package com.orderManagement.orderManagementService.prospect

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "orderDetails")
data class Prospect(val itemName: String, val quantity: Int, val paymentMode: String, val email: String, val status: String) {
    @Id
    lateinit var id: String
}