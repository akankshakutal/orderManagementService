package com.orderManagement.orderManagementService.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.kafka.common.serialization.Serializer

class PartitionIdSerializer<PartitionIdentifier> : Serializer<PartitionIdentifier> {
    override fun serialize(topic: String?, data: PartitionIdentifier?): ByteArray {
        return ObjectMapper().writeValueAsBytes(data)
    }

    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
    }

    override fun close() {}
}
