package com.orderManagement.orderManagementService.prospect

import com.orderManagement.orderManagementService.order.PaymentMode
import com.orderManagement.orderManagementService.order.Status
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@DataMongoTest
class ProspectRepositoryTest {
    @Autowired
    lateinit var prospectRepository: ProspectRepository

    @Test
    fun `save prospect`() {
        val prospect = Prospect("itemName", 3, PaymentMode.UPI, "email", Status.PLACED)
        prospectRepository.save(prospect).block()

        val savedProspect = prospectRepository.findAll().blockFirst()

        savedProspect shouldBe prospect
    }
}
