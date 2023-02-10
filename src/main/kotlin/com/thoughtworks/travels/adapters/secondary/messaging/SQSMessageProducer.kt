package com.thoughtworks.travels.adapters.secondary.messaging

import org.jetbrains.annotations.NotNull
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.stereotype.Component

@Component
class SQSMessageProducer<T>(
    private val queueMessagingTemplate: QueueMessagingTemplate,
) {
    fun send(queueName: String, @NotNull message: T) {
        queueMessagingTemplate.convertAndSend(
            queueName,
            message
        )
    }
}
