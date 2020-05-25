package com.orderManagement.orderManagementService.order

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.orderManagement.orderManagementService.utils.any
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@WebFluxTest(controllers = [OrderController::class])
class OrderControllerTest(@Autowired val testClient: WebTestClient) {

    @MockBean
    lateinit var orderService: OrderService

    @MockBean
    lateinit var orderValidator: OrderValidator

    private val orderDetails = OrderDetails("itemName", 3, PaymentMode.NET_BANKING, "email")

    @Test
    fun `should save order details and return it with 200 ok`() {
        val orderResponse = OrderResponse("", 2, PaymentMode.NET_BANKING, "email", Status.PLACED, "orderId")
        Mockito.`when`(orderService.order(any())).thenReturn(Mono.just(orderResponse))

        testClient.post()
                .uri("/create/order")
                .bodyValue(orderDetails)
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(jacksonObjectMapper().writeValueAsString(orderResponse))
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

        Mockito.verify(orderService, Mockito.times(1)).order(orderDetails)
    }

    @Test
    fun `should return all order details with 200 ok`() {
        val orderResponse = OrderResponse("", 2, PaymentMode.NET_BANKING, "email", Status.PLACED, "orderId")
        Mockito.`when`(orderService.getOrder()).thenReturn(Flux.just(orderResponse, orderResponse))

        testClient.get()
                .uri("/get/order")
                .exchange()
                .expectStatus().isOk
                .expectBody()
                .json(jacksonObjectMapper().writeValueAsString(listOf(orderResponse, orderResponse)))

        Mockito.verify(orderService, Mockito.times(1)).getOrder()
    }
}