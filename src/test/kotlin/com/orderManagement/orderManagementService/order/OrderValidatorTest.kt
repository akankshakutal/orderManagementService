package com.orderManagement.orderManagementService.order

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class OrderValidatorTest {
    private val orderValidator = OrderValidator()

    @Test
    fun `should throw invalid quantity exception`() {
        val orderDetails = OrderDetails("Laptop", -1, PaymentMode.NET_BANKING, "Email")
        assertThrows<InvalidQuantityException> {
            orderValidator.validate(orderDetails)
        }
    }
}
