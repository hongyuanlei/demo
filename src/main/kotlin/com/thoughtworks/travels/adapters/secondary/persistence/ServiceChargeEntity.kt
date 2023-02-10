package com.thoughtworks.travels.adapters.secondary.persistence

import com.thoughtworks.travels.domain.model.FulfilmentStatus
import java.math.BigDecimal
import java.time.OffsetDateTime
import javax.persistence.*

@Entity
@Table(name = "service_charge")
data class ServiceChargeEntity(
    @Id
    val id: String,
    @Column(name = "charge_amount", nullable = false, scale = 2)
    val chargeAmount: BigDecimal,
    @Column(name = "charge_currency", nullable = false)
    val chargeCurrency: String,
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    val status: FulfilmentStatus,
    @Column(name = "created_at", nullable = false)
    val createdAt: OffsetDateTime,
    @Column(name = "expired_at", nullable = false)
    val expiredAt: OffsetDateTime,
    @Column(name = "confirmed_at", nullable = true)
    val confirmedAt: OffsetDateTime?,
    @Column(name = "payment_id", nullable = true)
    val paymentId: String?,
    @Column(name = "payment_message", nullable = true)
    val paymentMessage: String?
)