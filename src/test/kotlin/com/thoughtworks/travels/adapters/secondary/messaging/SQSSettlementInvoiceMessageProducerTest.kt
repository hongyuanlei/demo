package com.thoughtworks.travels.adapters.secondary.messaging

import com.thoughtworks.travels.application.dto.InvoiceMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Tag("component")
@SpringBootTest
internal class SQSSettlementInvoiceMessageProducerTest {
    @Value("\${sqs.name.settlementInvoice}")
    lateinit var sqsName: String
    @Autowired
    private lateinit var sqsSettlementInvoiceMessageProducer: SQSSettlementInvoiceMessageProducer
    @Autowired
    private lateinit var queueMessagingTemplate: QueueMessagingTemplate

    @Test
    fun `should send settlement invoice message to queue`() {
        val settlementInvoice = InvoiceMessage(
            UUID.randomUUID().toString(),
            amount = BigDecimal("1000.00"),
            currency = "CNY",
            companyName = "Smith Enterprises",
            companyAddress = "456 Sierra Dr Mountain View 94039",
            invoiceDate = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
            dueDate = OffsetDateTime.of(2022, 3, 2, 3, 4, 5, 0, ZoneOffset.UTC),
        )

        sqsSettlementInvoiceMessageProducer.produceMessage(settlementInvoice)
        assertThat(queueMessagingTemplate.receiveAndConvert(sqsName, InvoiceMessage::class.java)).isEqualTo(
            settlementInvoice
        )
    }
}