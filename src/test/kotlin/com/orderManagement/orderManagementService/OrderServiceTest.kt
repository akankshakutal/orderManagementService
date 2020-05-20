package com.orderManagement.orderManagementService

import com.orderManagement.orderManagementService.prospect.Prospect
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

class OrderServiceTest {
    private val prospect = Prospect("itemName", 3, "paymentMode", "email", "PLACED")
    private val prospectRepository = mockk<ProspectRepository> {
        every { save<Prospect>(any()) } returns Mono.just(prospect)
    }
    private val orderService = OrderService(prospectRepository)

    @Test
    fun `should save order details to mongo`() {
        val orderDetails = OrderDetails("itemName", 3, "paymentMode", "email")
        orderService.order(orderDetails)

        verify(exactly = 1) {
            prospectRepository.save(Prospect("itemName", 3, "paymentMode", "email", "PLACED"))
        }
    }

}