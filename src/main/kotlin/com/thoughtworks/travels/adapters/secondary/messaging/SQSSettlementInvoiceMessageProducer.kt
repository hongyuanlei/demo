package com.thoughtworks.travels.adapters.secondary.messaging

import com.thoughtworks.travels.application.InvoiceMessageProducer
import com.thoughtworks.travels.application.dto.InvoiceMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class SQSSettlementInvoiceMessageProducer(
    @Value("\${sqs.name.settlementInvoice}") private val sqsName: String,
    private val sqsMessageProducer: SQSMessageProducer<InvoiceMessage>
) : InvoiceMessageProducer {
    override fun produceMessage(invoiceMessage: InvoiceMessage) =
        sqsMessageProducer.send(sqsName, invoiceMessage)
}