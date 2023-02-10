package com.thoughtworks.travels.adapters.secondary.persistence

import com.thoughtworks.travels.domain.model.FulfilmentStatus
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "settlement_invoice")
data class SettlementInvoiceEntity(
    @Id
    val id: String,
    @Column(name = "amount", nullable = false)
    val amount: BigDecimal,
    @Column(name = "invoice_date", nullable = false)
    val invoiceDate: OffsetDateTime,
    @Column(name = "due_date", nullable = false)
    val dueDate: OffsetDateTime,
    @Column(name = "company_name", nullable = false)
    val companyName: String,
    @Column(name = "company_address", nullable = false)
    val companyAddress: String,
    @Column(name = "charge_currency", nullable = false)
    val currency: String,
    @Column(name = "status", nullable = false)
    val status: FulfilmentStatus,
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime,
    @Column(name = "expired_at", nullable = false)
    val expiredAt: OffsetDateTime,
    @Column(name = "confirmed_at", nullable = true)
    val confirmedAt: OffsetDateTime?,
)