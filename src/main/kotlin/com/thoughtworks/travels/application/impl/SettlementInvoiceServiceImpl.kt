package com.thoughtworks.travels.application.impl

import com.thoughtworks.travels.application.InvoiceMessageProducer
import com.thoughtworks.travels.application.OffsetDateTimeProvider
import com.thoughtworks.travels.application.SettlementInvoiceService
import com.thoughtworks.travels.application.dto.InvoiceMessage
import com.thoughtworks.travels.domain.model.SettlementInvoice
import com.thoughtworks.travels.domain.model.SettlementInvoiceRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class SettlementInvoiceServiceImpl(
    @Value("\${platform.identifierPrefix.settlementInvoice}") private val identifierPrefix: String,
    private val settlementInvoiceRepository: SettlementInvoiceRepository,
    private val settlementInvoiceMessageProducer: InvoiceMessageProducer,
    private val dateTimeProvider: OffsetDateTimeProvider
) :
    SettlementInvoiceService {
    override fun confirmation(id: String) {
        val settlementInvoiceRequest =
            settlementInvoiceRepository.findById(id)
                ?: throw IllegalArgumentException("Settlement Invoice Request doesn't exist.")

        val confirmedAt = dateTimeProvider.now()
        if (!settlementInvoiceRequest.isExpiredAt(confirmedAt)) {
            settlementInvoiceMessageProducer.produceMessage(settlementInvoiceRequest.toInvoiceMessage(identifierPrefix))
            settlementInvoiceRequest.confirmationSuccess(confirmedAt)
        } else {
            settlementInvoiceRequest.confirmationFailed(confirmedAt)
        }
        settlementInvoiceRepository.save(settlementInvoiceRequest)
    }

    private fun SettlementInvoice.toInvoiceMessage(identifierPrefix: String) = InvoiceMessage(
        invoiceIdentifier = "${identifierPrefix}_$id",
        invoiceDate = invoiceDate,
        dueDate = dueDate,
        amount = amount.amount,
        currency = amount.currency.toString(),
        companyName = billTo.name,
        companyAddress = billTo.address
    )
}