package com.orderManagement.orderManagementService.order

import com.orderManagement.orderManagementService.kafka.Event
import com.orderManagement.orderManagementService.kafka.KafkaTopicProducer
import com.orderManagement.orderManagementService.prospect.Prospect
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.util.*

@Component
class OrderService(
        val prospectRepository: ProspectRepository,
        val kafkaTopicProducer: KafkaTopicProducer
) {
    @Value("\${spring.kafka.template.default-topic}")
    lateinit var topic: String

    fun order(orderDetails: Mono<OrderDetails>): Mono<OrderResponse> {
        return orderDetails.flatMap {
            val prospect = Prospect(it.itemName, it.quantity, it.paymentMode, it.email, Status.PLACED)
            prospectRepository.save(prospect)
        }.map {
            val event = Event(it.id, it.itemName, it.quantity, it.paymentMode, it.email)
            kafkaTopicProducer.produce(event, topic, UUID.randomUUID().toString())
                    .subscribeOn(Schedulers.elastic())
                    .subscribe()
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
