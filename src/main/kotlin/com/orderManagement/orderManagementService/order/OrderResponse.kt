package com.orderManagement.orderManagementService.order

data class OrderResponse(val itemName: String, val quantity: Int, val paymentMode: PaymentMode, val email: String, val status: Status, val orderId: String)
