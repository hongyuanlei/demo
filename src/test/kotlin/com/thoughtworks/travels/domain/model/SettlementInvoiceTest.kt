package com.thoughtworks.travels.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

internal class SettlementInvoiceTest {

    private lateinit var settlementInvoice: SettlementInvoice

    @BeforeEach
    fun beforeEach() {
        settlementInvoice = SettlementInvoice(
            id = "10001",
            amount = Money(Currency.getInstance("CNY"), BigDecimal("1000.00")),
            billTo = InvoiceCompany(
                name = "Smith Enterprises",
                address = "456 Sierra Dr Mountain View 94039",
            ),
            invoiceDate = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
            dueDate = OffsetDateTime.of(2022, 3, 2, 3, 4, 5, 0, ZoneOffset.UTC),
            status = FulfilmentStatus.REQUEST_SENT,
            createdAt = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
            expiredAt = OffsetDateTime.of(2022, 2, 7, 3, 4, 5, 0, ZoneOffset.UTC),
            confirmedAt = null
        )
    }

    @Test
    fun `should confirm settlement invoice success`() {
        settlementInvoice.confirmationSuccess(confirmedAt)

        assertThat(settlementInvoice.confirmedAt).isEqualTo(confirmedAt)
        assertThat(settlementInvoice.status).isEqualTo(FulfilmentStatus.REQUEST_CONFIRM_SUCCESS)
    }

    @Test
    fun `should confirm settlement invoice failed`() {
        settlementInvoice.confirmationFailed(confirmedAt)

        assertThat(settlementInvoice.confirmedAt).isEqualTo(confirmedAt)
        assertThat(settlementInvoice.status).isEqualTo(FulfilmentStatus.REQUEST_CONFIRM_FAILED)
    }

    companion object {
        private val confirmedAt = OffsetDateTime.of(2022, 3, 3, 3, 4, 5, 0, ZoneOffset.UTC)
    }
}