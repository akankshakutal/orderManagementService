package com.orderManagement.orderManagementService.order

data class OrderDetails(val itemName: String, val quantity: Int, val paymentMode: PaymentMode, val email: String)

enum class PaymentMode {
    NET_BANKING,
    CASH_ON_DELIVERY,
    UPI
}

enum class Status {
    PLACED,
    DELIVERED
}