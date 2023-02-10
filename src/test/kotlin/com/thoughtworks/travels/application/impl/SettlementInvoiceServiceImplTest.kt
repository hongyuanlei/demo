package com.thoughtworks.travels.application.impl

import com.thoughtworks.travels.application.InvoiceMessageProducer
import com.thoughtworks.travels.application.OffsetDateTimeProvider
import com.thoughtworks.travels.application.SettlementInvoiceService
import com.thoughtworks.travels.application.dto.InvoiceMessage
import com.thoughtworks.travels.domain.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class SettlementInvoiceServiceImplTest {
    @Mock
    private lateinit var settlementInvoiceRepository: SettlementInvoiceRepository

    @Mock
    private lateinit var settlementInvoiceMessageProducer: InvoiceMessageProducer

    @Mock
    private lateinit var offsetDateTimeProvider: OffsetDateTimeProvider

    private val settlementInvoiceService: SettlementInvoiceService by lazy {
        SettlementInvoiceServiceImpl(
            identifierPrefix = identifierPrefix,
            settlementInvoiceRepository,
            settlementInvoiceMessageProducer,
            dateTimeProvider = offsetDateTimeProvider
        )
    }

    @Test
    fun `Given settlement invoice request does not exist, When platform confirm request, Then confirmation failed`() {
        val id = "10001"
        whenever(settlementInvoiceRepository.findById(id)).thenReturn(null)
        val exception = assertThrows<IllegalArgumentException> {
            settlementInvoiceService.confirmation(id)
        }

        assertThat(exception.message).isEqualTo("Settlement Invoice Request doesn't exist.")
    }

    @Test
    fun `Given settlement invoice request not expired, When platform confirm request, Then confirmation success`() {
        val id = "10001"
        val invoiceDate = offsetDateTimeOf(2022, 2, 2)
        val dueDate = offsetDateTimeOf(2022, 3, 2)
        val createdAt = offsetDateTimeOf(2022, 2, 2)
        val expiredAt = offsetDateTimeOf(2022, 2, 7)
        val confirmedAt = offsetDateTimeOf(2022, 2, 7)
        whenever(settlementInvoiceRepository.findById(id)).thenReturn(
            SettlementInvoice(
                id = id,
                amount = amount,
                billTo = InvoiceCompany(
                    name = companyName,
                    address = companyAddress,
                ),
                invoiceDate = invoiceDate,
                dueDate = dueDate,
                status = FulfilmentStatus.REQUEST_SENT,
                createdAt = createdAt,
                expiredAt = expiredAt,
                confirmedAt = null
            )
        )
        whenever(offsetDateTimeProvider.now()).thenReturn(confirmedAt)

        settlementInvoiceService.confirmation(id)

        verify(
            settlementInvoiceMessageProducer,
            times(1)
        ).produceMessage(
            InvoiceMessage(
                invoiceIdentifier = "${identifierPrefix}_$id",
                invoiceDate = invoiceDate,
                dueDate = dueDate,
                amount = amount.amount,
                currency = amount.currency.toString(),
                companyName = companyName,
                companyAddress = companyAddress
            )
        )

        verify(settlementInvoiceRepository, times(1)).save(
            SettlementInvoice(
                id = id,
                amount = amount,
                billTo = InvoiceCompany(
                    name = companyName,
                    address = companyAddress,
                ),
                invoiceDate = invoiceDate,
                dueDate = dueDate,
                status = FulfilmentStatus.REQUEST_CONFIRM_SUCCESS,
                createdAt = createdAt,
                expiredAt = expiredAt,
                confirmedAt = confirmedAt
            )
        )
    }

    @Test
    fun `Given settlement invoice request expired, When platform confirm request, Then confirmation failed`() {
        val id = "10001"
        val invoiceDate = offsetDateTimeOf(2022, 2, 2)
        val dueDate = offsetDateTimeOf(2022, 3, 2)
        val createdAt = offsetDateTimeOf(2022, 2, 2)
        val expiredAt = offsetDateTimeOf(2022, 2, 7)
        val confirmedAt = offsetDateTimeOf(2022, 2, 8)
        whenever(settlementInvoiceRepository.findById(id)).thenReturn(
            SettlementInvoice(
                id = id,
                amount = amount,
                billTo = InvoiceCompany(
                    name = companyName,
                    address = companyAddress,
                ),
                invoiceDate = invoiceDate,
                dueDate = dueDate,
                status = FulfilmentStatus.REQUEST_SENT,
                createdAt = createdAt,
                expiredAt = expiredAt,
                confirmedAt = null
            )
        )

        whenever(offsetDateTimeProvider.now()).thenReturn(confirmedAt)

        settlementInvoiceService.confirmation(id)

        verify(
            settlementInvoiceMessageProducer,
            times(0)
        ).produceMessage(any())

        verify(settlementInvoiceRepository, times(1)).save(
            SettlementInvoice(
                id = id,
                amount = amount,
                billTo = InvoiceCompany(
                    name = companyName,
                    address = companyAddress,
                ),
                invoiceDate = invoiceDate,
                dueDate = dueDate,
                status = FulfilmentStatus.REQUEST_CONFIRM_FAILED,
                createdAt = createdAt,
                expiredAt = expiredAt,
                confirmedAt = confirmedAt
            )
        )
    }

    private fun offsetDateTimeOf(year: Number, month: Number, day: Number): OffsetDateTime {
        return OffsetDateTime.of(year.toInt(), month.toInt(), day.toInt(), 3, 4, 5, 0, ZoneOffset.UTC)
    }

    companion object {
        private const val identifierPrefix = "REN_NI_XIN_SETTLEMENT_INVOICE"
        private const val companyName = "Smith Enterprises"
        private const val companyAddress = "456 Sierra Dr Mountain View 94039"
        private val amount = Money(Currency.getInstance("CNY"), BigDecimal("1000.00"))
    }
}