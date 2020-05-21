package com.orderManagement.orderManagementService.order

import com.orderManagement.orderManagementService.prospect.Prospect
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier.withVirtualTime

class OrderServiceTest {
    private val prospect = Prospect("itemName", 3, PaymentMode.NET_BANKING, "email", Status.PLACED)
    private val prospectRepository = mockk<ProspectRepository> {
        every { save<Prospect>(any()) } returns Mono.just(prospect)
    }
    private val orderService = OrderService(prospectRepository)

    @Test
    fun `should save order details to mongo`() {
        val orderDetails = OrderDetails("itemName", 3, PaymentMode.CASH_ON_DELIVERY, "email")
        orderService.order(Mono.just(orderDetails))
        val prospect = Prospect("itemName", 3, PaymentMode.CASH_ON_DELIVERY, "email", Status.DELIVERED)

        withVirtualTime { orderService.order(orderDetails = Mono.just(orderDetails)) }
                .consumeNextWith {
                    verify(exactly = 1) { prospectRepository.save(prospect) }
                }.verifyComplete()
    }

}