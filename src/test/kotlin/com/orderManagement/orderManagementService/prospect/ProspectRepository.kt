package com.orderManagement.orderManagementService.prospect

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
    fun `find prospect by partner identifier`() {
        val prospect = Prospect("itemName", 3, "paymentMode", "email", "PLACED")
        prospectRepository.save(prospect).block()

        val savedProspect = prospectRepository.findAll().blockFirst()

        savedProspect shouldBe prospect
    }
}
