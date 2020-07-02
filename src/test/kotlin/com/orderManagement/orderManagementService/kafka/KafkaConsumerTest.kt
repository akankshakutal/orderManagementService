package com.orderManagement.orderManagementService.kafka

import com.orderManagement.orderManagementService.order.PaymentMode
import com.orderManagement.orderManagementService.order.Status
import com.orderManagement.orderManagementService.prospect.Prospect
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver

class KafkaConsumerTest {
    private val prospectRepository = mockk<ProspectRepository>()
    private val kafkaReceiver = mockk<KafkaReceiver<PartitionIdentifier, PaymentEvent>> {
        every { receive() } returns Flux.empty()
    }

    @Test
    fun `should return the result`() {
        val kafkaTopicConsumer = KafkaConsumer(kafkaReceiver, prospectRepository)

        kafkaTopicConsumer.run(null)

        verify(exactly = 1) { kafkaReceiver.receive() }
    }

    @Test
    fun `should save order details to mongo `() {
        val prospectBeforeSave = Prospect("orderId", 1, 2000, PaymentMode.CASH_ON_DELIVERY, "", Status.PLACED)
                .apply { id = "abcd1234" }
        every { prospectRepository.findById(any<String>()) } returns Mono.just(prospectBeforeSave)
        val kafkaTopicConsumer = KafkaConsumer(kafkaReceiver, prospectRepository)

        kafkaTopicConsumer.process(PaymentEvent("abcd1234",2000,"PAID"))

        verify(exactly = 1) { prospectRepository.findById("abcd1234") }
    }
}