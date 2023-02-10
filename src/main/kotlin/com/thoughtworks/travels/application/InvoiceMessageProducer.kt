package com.thoughtworks.travels.application

import com.thoughtworks.travels.application.dto.InvoiceMessage

interface InvoiceMessageProducer {
    fun produceMessage(invoiceMessage: InvoiceMessage)
}