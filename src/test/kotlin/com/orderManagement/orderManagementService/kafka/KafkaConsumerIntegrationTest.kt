package com.orderManagement.orderManagementService.kafka

import com.orderManagement.orderManagementService.order.PaymentMode
import com.orderManagement.orderManagementService.order.Status
import com.orderManagement.orderManagementService.prospect.Prospect
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import io.kotlintest.shouldBe
import org.junit.jupiter.api.AfterEach
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
@EmbeddedKafka(controlledShutdown = true, brokerProperties = ["log.dir=out/embedded-kafka/paymentService"])
class KafkaConsumerIntegrationTest(@Autowired val testKafkaProducer: TestKafkaProducer) {
    @Autowired
    private lateinit var kafkaConsumer: KafkaConsumer

    @Autowired
    private lateinit var prospectRepository: ProspectRepository

    @AfterEach
    internal fun tearDown() {
        prospectRepository.deleteAll().block()
    }

    @Test
    fun `should consumer kafka events`() {
        val prospect = Prospect("laptop", 1, 1000, PaymentMode.CASH_ON_DELIVERY, "email", Status.PLACED)
                .apply { id = "abcd1234" }
        prospectRepository.save(prospect).block()

        kafkaConsumer.setCountDownLatch(1)
        val event = Event("abcd1234", PaymentMode.NET_BANKING, 2000)

        testKafkaProducer.produce(event, "paymentDetails", "abcd1234").subscribe()

        kafkaConsumer.countDownLatch.await(5, TimeUnit.SECONDS)

        val savedDetails = prospectRepository.findById("abcd1234").block()

        savedDetails shouldBe Prospect("laptop", 1, 1000, PaymentMode.CASH_ON_DELIVERY, "email", Status.DELIVERED)
    }
}