package com.orderManagement.orderManagementService.prospect

import org.springframework.data.mongodb.repository.ReactiveMongoRepository

interface ProspectRepository : ReactiveMongoRepository<Prospect, String> {
}