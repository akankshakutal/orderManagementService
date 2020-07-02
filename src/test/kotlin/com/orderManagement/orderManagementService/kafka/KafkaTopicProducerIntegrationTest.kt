package com.orderManagement.orderManagementService.kafka

import com.orderManagement.orderManagementService.order.PaymentMode
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension::class)
@EmbeddedKafka(controlledShutdown = true, brokerProperties = ["log.dir=out/embedded-kafka/orderService"])
class KafkaTopicProducerIntegrationTest(@Autowired val testKafkaConsumer: TestKafkaConsumer) {
    @Autowired
    private lateinit var kafkaTopicProducer: KafkaTopicProducer

    @Test
    fun `should produce event`() {
        val event = Event("Id", PaymentMode.NET_BANKING, 1000)

        kafkaTopicProducer.produce(event, "orderDetails", "abcd1234").subscribe()

        testKafkaConsumer.countDownLatch.await(20, TimeUnit.SECONDS)
        testKafkaConsumer.run(null)
        val receivedMessages = testKafkaConsumer.messageList

        receivedMessages.size shouldBe 1
        receivedMessages[0] shouldBe event
    }
}
