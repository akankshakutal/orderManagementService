package com.orderManagement.orderManagementService

data class OrderResponse(val itemName: String, val quantity: Int, val paymentMode: String, val email: String, val status: String)
