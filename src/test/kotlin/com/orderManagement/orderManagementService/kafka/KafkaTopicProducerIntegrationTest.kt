package com.orderManagement.orderManagementService.kafka

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.orderManagement.orderManagementService.order.PaymentMode
import io.kotlintest.shouldBe
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.header.Headers
import org.apache.kafka.common.serialization.Deserializer
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.util.context.Context
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(SpringExtension::class)
@EmbeddedKafka(controlledShutdown = true, brokerProperties = ["log.dir=out/embedded-kafka/orderService"])
class KafkaTopicProducerIntegrationTest {
    @Autowired
    private lateinit var kafkaTopicProducer: KafkaTopicProducer

    @Test
    @Disabled
    fun `should produce event`() {
        val testKafkaConsumer = TestKafkaConsumer()
        val event = Event("Id", "itemName", 3, PaymentMode.NET_BANKING, "email")

        kafkaTopicProducer.produce(event, "orderDetails", "abcd1234").subscribe()

        testKafkaConsumer.countDownLatch.await(20, TimeUnit.SECONDS)
        testKafkaConsumer.run(null)
        val receivedMessages = testKafkaConsumer.messageList

        receivedMessages.size shouldBe 1
    }
}

class TestKafkaConsumer : ApplicationRunner {
    var countDownLatch = CountDownLatch(1)
    var messageList = mutableListOf<Map<String, Any>>()

    private fun getKafkaReceiver(): KafkaReceiver<String, ByteArray> {
        val properties = mutableMapOf<String, Any>(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to "localhost:9092",
                ConsumerConfig.CLIENT_ID_CONFIG to "payment",
                ConsumerConfig.GROUP_ID_CONFIG to "order-event-group-id",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to PartitionIdDeserializer::class.java,
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG to false,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to JsonToHashMapDeserializer::class.java
        )
        val receiverOptions = ReceiverOptions.create<String, ByteArray>(properties)
                .subscription(listOf("orderDetails"))

        return KafkaReceiver.create(receiverOptions)
    }

    override fun run(args: ApplicationArguments?) {
        getKafkaReceiver().receive()
                .flatMapSequential { receiverRecord ->
                    val jsonToHashMapDeserializer = JsonToHashMapDeserializer()
                    val deserializeValue = jsonToHashMapDeserializer.deserialize("orderDetails", receiverRecord.value())!!
                    process(deserializeValue)
                            .subscriberContext(getContextFromKafkaHeader(receiverRecord.headers()))
                            .flatMap { receiverRecord.receiverOffset().toMono() }
                }
                .flatMap { it.commit() }
                .subscribe()
    }

    private fun getContextFromKafkaHeader(headers: Headers): Context {
        var context = Context.empty()
        val httpHeaders = HttpHeaders()

        when {
            headers.count() != 0 -> {
                headers.iterator().forEach { httpHeaders[it.key()] = listOf(String(it.value())) }
                context = context.put("headers", httpHeaders)
            }
        }
        return context
    }

    private fun process(message: Map<String, Any>): Mono<Boolean> {
        return Mono.subscriberContext().map {
            messageList.add(message)
            countDownLatch.countDown()
        }.map { true }
    }
}

class JsonToHashMapDeserializer : Deserializer<Map<String, Any>> {

    override fun deserialize(topic: String?, data: ByteArray): Map<String, Any>? {
        return jacksonObjectMapper().readValue(data, Map::class.java) as Map<String, Any>
    }

}

class PartitionIdDeserializer : Deserializer<PartitionIdentifier> {
    override fun deserialize(topic: String?, data: ByteArray?): PartitionIdentifier {
        return jacksonObjectMapper().readValue(data, PartitionIdentifier::class.java)
    }

}
