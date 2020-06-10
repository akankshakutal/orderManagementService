package com.orderManagement.orderManagementService.kafka

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class KafkaConfig {
    @Value("\${spring.kafka.bootstrap-servers}")
    lateinit var kafkaUrl: String

    @Value("\${spring.kafka.listener.ack-mode}")
    lateinit var ackConfig: String

    @Value("\${spring.kafka.admin.client-id}")
    lateinit var clientId: String
}
