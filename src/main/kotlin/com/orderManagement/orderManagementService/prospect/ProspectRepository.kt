package com.orderManagement.orderManagementService.prospect

import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ProspectRepository : ReactiveMongoRepository<Prospect, String> {
    override fun findById(id: String): Mono<Prospect>
}