package com.thoughtworks.travels.adapters.secondary.messaging

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration
import com.amazonaws.services.sqs.AmazonSQSAsync
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class MessagingConfiguration {
    @Value("\${cloud.aws.sqs.endpoint}")
    private lateinit var endpoint: String

    @Value("\${cloud.aws.region.static}")
    private lateinit var region: String

    @Bean
    fun amazonSQSAsync(): AmazonSQSAsync {
        return AmazonSQSAsyncClientBuilder.standard()
            .withEndpointConfiguration(EndpointConfiguration(endpoint, region))
            .build()
    }

    @Bean
    fun queueMessagingTemplate(amazonSQSAsync: AmazonSQSAsync): QueueMessagingTemplate =
        QueueMessagingTemplate(amazonSQSAsync)
}