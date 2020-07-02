package com.orderManagement.orderManagementService.kafka

import com.orderManagement.orderManagementService.order.Status
import com.orderManagement.orderManagementService.prospect.ProspectRepository
import org.apache.kafka.common.header.Headers
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.receiver.KafkaReceiver
import reactor.util.context.Context
import java.util.concurrent.CountDownLatch

@Component
class KafkaConsumer(val kafkaReceiver: KafkaReceiver<PartitionIdentifier, PaymentEvent>,
                    val prospectRepository: ProspectRepository) : ApplicationRunner {
    var countDownLatch = CountDownLatch(1)

    override fun run(args: ApplicationArguments?) {
        kafkaReceiver.receive()
                .flatMapSequential { receiverRecord ->
                    process(receiverRecord.value() as PaymentEvent)
                            .subscriberContext(getContextFromKafkaHeader(receiverRecord.headers()))
                            .flatMap { Mono.just(receiverRecord.receiverOffset()) }
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

    fun process(message: PaymentEvent): Mono<Boolean> {
        return prospectRepository.findById(message.orderId)
                .flatMap { prospect ->
                    prospect.status = Status.DELIVERED
                    prospectRepository.save(prospect)
                }
                .map { countDownLatch.countDown() }.map { true }
    }

    fun setCountDownLatch(count: Int) {
        countDownLatch = CountDownLatch(count)
    }
}

data class PaymentEvent(val orderId: String, val amount: Int, val status: String)