package com.orderManagement.orderManagementService.order

import com.orderManagement.orderManagementService.utils.any
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(controllers = [OrderController::class])
class OrderControllerAdviceTest(
        @Autowired
        val testClient: WebTestClient) {

    @MockBean
    lateinit var orderValidator: OrderValidator

    @MockBean
    lateinit var orderService: OrderService

    @Test
    fun `should return error response for Invalid Quantity Exception`() {
        val orderDetails = OrderDetails("itemName", -3, "paymentMode", "email")

        Mockito.`when`(orderValidator.validate(any())).thenReturn(Mono.error(InvalidQuantityException()))
        Mockito.`when`(orderService.order(any())).thenReturn(Mono.empty())

        testClient.post()
                .uri("/create/order")
                .bodyValue(orderDetails)
                .exchange()
                .expectStatus().isOk
                .expectBody()
    }
}