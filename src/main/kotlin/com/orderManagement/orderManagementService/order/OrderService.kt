package com.orderManagement.orderManagementService.order

import com.orderManagement.orderManagementService.kafka.Event
import com.orderManagement.orderManagementService.kafka.KafkaTopicProducer
import com.orderManagement.orderManagementService.prospect.Prospect
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class OrderService(
        val prospectRepository: ProspectRepository,
        val kafkaTopicProducer: KafkaTopicProducer
) {
    @Value("\${spring.kafka.template.default-topic}")
    lateinit var topic: String

    fun order(orderDetails: Mono<OrderDetails>): Mono<OrderResponse> {
        return orderDetails.flatMap {
            val itemName = it.itemName
            val quantity = it.quantity
            val paymentMode = it.paymentMode
            val email = it.email
            prospectRepository.save(Prospect(itemName, quantity, paymentMode, email, Status.PLACED))
        }.map {
            val event = Event(it.id, it.itemName, it.quantity, it.paymentMode, it.email)
            kafkaTopicProducer.produce(event, topic, "abcd1234")
            OrderResponse(it.itemName, it.quantity, it.paymentMode, it.email, it.status, it.id)
        }
    }

    fun getOrder(): Flux<OrderResponse> {
        return prospectRepository
                .findAll()
                .map { OrderResponse(it.itemName, it.quantity, it.paymentMode, it.email, it.status, it.id) }
    }

    fun getOrderDetailsFor(orderId: String): Mono<OrderResponse> {
        return prospectRepository
                .findById(orderId)
                .map { OrderResponse(it.itemName, it.quantity, it.paymentMode, it.email, it.status, it.id) }
    }
}
