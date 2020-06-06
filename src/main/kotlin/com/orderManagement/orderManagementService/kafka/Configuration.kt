package com.orderManagement.orderManagementService.kafka

import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import java.util.*

@Configuration
class Configuration {

    @Bean
    fun kafkaSender(kafkaConfig: KafkaConfig): KafkaSender<PartitionIdentifier, Event>? {
        val props = HashMap<String, Any>()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaConfig.kafkaUrl
        props[ProducerConfig.CLIENT_ID_CONFIG] = kafkaConfig.clientId + UUID.randomUUID().toString()
        props[ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION] = 1
        props[ProducerConfig.ACKS_CONFIG] = kafkaConfig.ackConfig
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = PartitionIdSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = EventSerializer::class.java

        val senderOptions = SenderOptions.create<PartitionIdentifier, Event>(props)
        return KafkaSender.create(senderOptions)
    }
}