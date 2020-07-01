package com.orderManagement.orderManagementService.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.orderManagement.orderManagementService.order.PaymentMode
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import java.util.*

@Component
class KafkaTopicProducer(val kafkaSender: KafkaSender<PartitionIdentifier, Event>) {

    fun produce(data: Event, topicName: String, partitionIdentifier: String): Mono<Boolean> {
        return Mono.subscriberContext().flatMap {
            val headers1 = createProducerRecordWithHeaders(data, topicName, partitionIdentifier)
            val senderRecord = SenderRecord.create(headers1, UUID.randomUUID().toString())
            kafkaSender.send(Mono.just(senderRecord)).next()
        }.map { true }.onErrorReturn(false)
    }

    private fun createProducerRecordWithHeaders(data: Event, topicName: String, partitionIdentifier: String)
            : ProducerRecord<PartitionIdentifier, Event> {
        return when {
            partitionIdentifier.isBlank() -> ProducerRecord(topicName, data)
            else -> ProducerRecord(topicName, PartitionIdentifier(partitionIdentifier), data)
        }
    }
}

data class Event(
        val orderId: String,
        val paymentMode: PaymentMode,
        val amount: Int
)

data class PartitionIdentifier(val id: String)

class EventSerializer<T> : Serializer<T> {
    override fun serialize(topic: String?, data: T?): ByteArray {
        return ObjectMapper().writeValueAsBytes(data)
    }
}
