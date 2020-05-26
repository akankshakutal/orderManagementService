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
            .apply { id = "abcd1234" }
    private val prospectRepository = mockk<ProspectRepository>()
    private val orderService = OrderService(prospectRepository)

    @Test
    fun `should save order details to mongo`() {
        every { prospectRepository.save<Prospect>(any()) } returns Mono.just(prospect)
        val orderDetails = OrderDetails("itemName", 3, PaymentMode.NET_BANKING, "email")

        val order = orderService.order(Mono.just(orderDetails))

        withVirtualTime { order }
                .consumeNextWith { verify { prospectRepository.save(prospect) } }
                .verifyComplete()
    }

    @Test
    fun `should call prospectRepository`() {
        every { prospectRepository.findAll() } returns Flux.just(prospect)

        val orderDetails = orderService.getOrder()

        withVirtualTime { orderDetails }
                .consumeNextWith { verify { prospectRepository.findAll() } }
                .verifyComplete()
    }

    @Test
    fun `should get order details for given orderId`() {
        val orderId = "abcd1234"
        every { prospectRepository.save<Prospect>(any()) } returns Mono.just(prospect)
        every { prospectRepository.findById(any<String>()) } returns Mono.just(prospect)
        prospectRepository.save(prospect).block()

        val orderDetails = orderService.getOrderDetailsFor(orderId)

        withVirtualTime { orderDetails }
                .consumeNextWith { verify { prospectRepository.findById(orderId) } }
                .verifyComplete()
    }
}