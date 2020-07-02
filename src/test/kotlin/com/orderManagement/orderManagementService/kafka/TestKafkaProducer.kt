package com.orderManagement.orderManagementService.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.Serializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import java.util.*

@Component
@ActiveProfiles("test")
class TestKafkaProducer {

    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var kafkaUrl: String

    @Value("\${spring.kafka.consumer.client-id}")
    lateinit var clientId: String

    fun produce(data: PaymentEvent, topicName: String, partitionIdentifier: String): Mono<Boolean> {
        return Mono.subscriberContext().flatMap {
            val headers1 = createProducerRecordWithHeaders(data, topicName, partitionIdentifier)
            val senderRecord = SenderRecord.create(headers1, UUID.randomUUID().toString())
            getKafkaSender().send(Mono.just(senderRecord)).next()
        }.map { true }.onErrorReturn(false)
    }

    private fun createProducerRecordWithHeaders(data: PaymentEvent, topicName: String, partitionIdentifier: String)
            : ProducerRecord<PartitionIdentifier, PaymentEvent> {
        return when {
            partitionIdentifier.isBlank() -> ProducerRecord(topicName, data)
            else -> ProducerRecord(topicName, PartitionIdentifier(partitionIdentifier), data)
        }
    }

    fun getKafkaSender(): KafkaSender<PartitionIdentifier, PaymentEvent> {
        val props = HashMap<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaUrl
        props[ProducerConfig.CLIENT_ID_CONFIG] = clientId
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = 1
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = PartitionIdSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = PaymentEventSerializer::class.java

        val senderOptions = SenderOptions.create<PartitionIdentifier, PaymentEvent>(props)
        return KafkaSender.create(senderOptions)
    }
}

class PaymentEventSerializer<T> : Serializer<T> {
    override fun serialize(topic: String?, data: T?): ByteArray = jacksonObjectMapper().writeValueAsBytes(data)
}

class PartitionIdSerializer<PartitionIdentifier> : Serializer<PartitionIdentifier> {
    override fun serialize(topic: String?, data: PartitionIdentifier?): ByteArray {
        return ObjectMapper().writeValueAsBytes(data)
    }
}
