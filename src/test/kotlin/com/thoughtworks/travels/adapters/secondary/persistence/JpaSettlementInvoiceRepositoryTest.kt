package com.thoughtworks.travels.adapters.secondary.persistence

import com.thoughtworks.travels.domain.model.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Tag("component")
@SpringBootTest
internal class JpaSettlementInvoiceRepositoryTest {

    @Autowired
    private lateinit var settlementInvoiceRepository: SettlementInvoiceRepository

    @Test
    fun `should save settlement invoice`() {
        val settlementInvoices = (0..2).map { createSettlementInvoice() }

        settlementInvoices.forEach {
            settlementInvoiceRepository.save(it)
        }

        settlementInvoices.forEach {
            val settlementInvoice = settlementInvoiceRepository.findById(it.id)
            assertThat(settlementInvoice).isNotNull
            assertThat(settlementInvoice?.id).isEqualTo(it.id)
            assertThat(settlementInvoice?.amount).isEqualTo(it.amount)
            assertThat(settlementInvoice?.billTo).isEqualTo(it.billTo)
            assertThat(settlementInvoice?.invoiceDate).isEqualTo(it.invoiceDate)
            assertThat(settlementInvoice?.dueDate).isEqualTo(it.dueDate)
            assertThat(settlementInvoice?.status).isEqualTo(it.status)
            assertThat(settlementInvoice?.createdAt).isEqualTo(it.createdAt)
            assertThat(settlementInvoice?.expiredAt).isEqualTo(it.expiredAt)
            assertThat(settlementInvoice?.confirmedAt).isEqualTo(it.confirmedAt)
        }
    }

    private fun createSettlementInvoice(): SettlementInvoice {
        return SettlementInvoice(
            id = UUID.randomUUID().toString(),
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
}