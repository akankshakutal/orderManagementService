package com.orderManagement.orderManagementService.prospect

import com.orderManagement.orderManagementService.prospect.Prospect
import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ProspectRepository : ReactiveMongoRepository<Prospect, String> {
}