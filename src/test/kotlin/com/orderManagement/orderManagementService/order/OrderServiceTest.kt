package com.orderManagement.orderManagementService.order

import com.orderManagement.orderManagementService.prospect.Prospect
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier.withVirtualTime

class OrderServiceTest {
    private val prospect = Prospect("itemName", 3, PaymentMode.NET_BANKING, "email", Status.PLACED)
    private val prospectRepository = mockk<ProspectRepository> {
        every { save<Prospect>(any()) } returns Mono.just(prospect)
        every { findAll() } returns Flux.just(prospect)
    }
    private val orderService = OrderService(prospectRepository)

    @Test
    fun `should save order details to mongo`() {
        prospect.id = "abcd1234"
        val orderDetails = OrderDetails("itemName", 3, PaymentMode.CASH_ON_DELIVERY, "email")
        orderService.order(Mono.just(orderDetails))
        val prospect = Prospect("itemName", 3, PaymentMode.CASH_ON_DELIVERY, "email", Status.DELIVERED)
        prospect.id = "abcd1234"

        withVirtualTime { orderService.order(orderDetails = Mono.just(orderDetails)) }
                .consumeNextWith {
                    verify(exactly = 1) { prospectRepository.save(prospect) }
                }.verifyComplete()
    }

    @Test
    fun `should get all the orders`() {
        prospect.id = "abcd1234"
        withVirtualTime { orderService.getOrder() }
                .consumeNextWith {
                    verify(exactly = 1) { prospectRepository.findAll() }
                }.verifyComplete()

    }
}