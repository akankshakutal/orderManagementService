package com.orderManagement.orderManagementService

import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

@WebFluxTest(controllers = [OrderController::class])
class OrderControllerTest(@Autowired val testClient: WebTestClient) {

    @MockBean
    lateinit var orderService: OrderService
    @MockBean
    lateinit var orderValidator: OrderValidator

    private val orderDetails = OrderDetails("itemName", 3, "paymentMode", "email")

    @Test
    fun `should respond with 200 OK`() {
        testClient.post()
                .uri("/create/order")
                .bodyValue(orderDetails)
                .exchange()
                .expectStatus().isOk
    }

    @Test
    fun `should call orderService`() {
        val orderDetails = Mono.just(orderDetails)
        Mockito.`when`(orderValidator.validate(any())).thenReturn(orderDetails)
        testClient.post()
                .uri("/create/order")
                .bodyValue(this.orderDetails)
                .exchange()
                .expectStatus().isOk

        Mockito.verify(orderService, Mockito.times(1))
                .order(orderDetails)
    }

    @Test
    fun `should call OrderValidator`() {
        testClient.post()
                .uri("/create/order")
                .bodyValue(orderDetails)
                .exchange()
                .expectStatus().isOk

        Mockito.verify(orderValidator, Mockito.times(1))
                .validate(orderDetails)
    }
}