package com.orderManagement.orderManagementService.prospect

import com.orderManagement.orderManagementService.order.PaymentMode
import com.orderManagement.orderManagementService.order.Status
import io.kotlintest.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@DataMongoTest
class ProspectRepositoryTest {
    @Autowired
    lateinit var prospectRepository: ProspectRepository

    @AfterEach
    fun tearDown() {
        prospectRepository.deleteAll().block()
    }

    @Test
    fun `save prospect`() {
        val prospect = Prospect("itemName", 3, 3000, PaymentMode.UPI, "email", Status.PLACED)
        prospectRepository.save(prospect).block()

        val savedProspect = prospectRepository.findAll().blockFirst()

        savedProspect shouldBe prospect
    }

    @Test
    fun `should find document by orderId`() {
        val prospect = Prospect("orderId", 2, 2000, PaymentMode.CASH_ON_DELIVERY, "email", Status.PLACED)
                .apply { id = "1234" }
        prospectRepository.save(prospect).block()

        val savedProspect = prospectRepository.findById("1234").block()

        savedProspect shouldBe prospect
    }
}
