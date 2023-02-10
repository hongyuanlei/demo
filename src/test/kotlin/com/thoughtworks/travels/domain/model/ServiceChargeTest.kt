package com.thoughtworks.travels.domain.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

internal class ServiceChargeTest {

    private lateinit var serviceCharge: ServiceCharge

    @BeforeEach
    fun beforeEach() {
        serviceCharge = ServiceCharge(
            id = UUID.randomUUID().toString(),
            status = FulfilmentStatus.REQUEST_SENT,
            amount = Money(Currency.getInstance("CNY"), BigDecimal("1000.00")),
            createdAt = OffsetDateTime.of(2022, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC),
            expiredAt = OffsetDateTime.of(2022, 1, 7, 3, 4, 5, 0, ZoneOffset.UTC),
            confirmedAt = null,
            paymentId = null,
            paymentMessage = null
        )
    }

    @Test
    fun `confirm service charge success`() {
        val confirmedAt = OffsetDateTime.of(2022, 1, 3, 3, 4, 5, 0, ZoneOffset.UTC)
        val paymentId = "123414525452341321423"
        serviceCharge.confirm(
            PaymentConfirmation(
                paymentId = paymentId,
                confirmedAt = confirmedAt,
                isSuccess = true,
                message = null
            )
        )

        assertThat(serviceCharge.status).isEqualTo(FulfilmentStatus.REQUEST_CONFIRM_SUCCESS)
        assertThat(serviceCharge.confirmedAt).isEqualTo(confirmedAt)
        assertThat(serviceCharge.paymentId).isEqualTo(paymentId)
        assertThat(serviceCharge.paymentMessage).isNull()
    }

    @Test
    fun `confirm service charge failed`() {
        val confirmedAt = OffsetDateTime.of(2022, 1, 3, 3, 4, 5, 0, ZoneOffset.UTC)
        val paymentId = "123414525452341321423"
        val message = "failed message"
        serviceCharge.confirm(
            PaymentConfirmation(
                paymentId = paymentId,
                confirmedAt = confirmedAt,
                isSuccess = false,
                message = message
            )
        )

        assertThat(serviceCharge.status).isEqualTo(FulfilmentStatus.REQUEST_CONFIRM_FAILED)
        assertThat(serviceCharge.confirmedAt).isEqualTo(confirmedAt)
        assertThat(serviceCharge.paymentId).isEqualTo(paymentId)
        assertThat(serviceCharge.paymentMessage).isEqualTo(message)
    }
}