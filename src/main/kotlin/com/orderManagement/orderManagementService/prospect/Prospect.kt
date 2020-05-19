package com.orderManagement.orderManagementService.prospect

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document(collection = "orderDetails")
class Prospect(val itemName: String, val quantity: Int, val paymentMode: String,val email: String) {
    @Id
    lateinit var id: String
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Prospect

        if (itemName != other.itemName) return false
        if (quantity != other.quantity) return false
        if (paymentMode != other.paymentMode) return false
        if (email != other.email) return false

        return true
    }

    override fun hashCode(): Int {
        var result = itemName.hashCode()
        result = 31 * result + quantity
        result = 31 * result + paymentMode.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }

}