package com.thoughtworks.travels.adapters.secondary.persistence

import com.thoughtworks.travels.domain.model.FulfilmentStatus
import com.thoughtworks.travels.domain.model.Money
import com.thoughtworks.travels.domain.model.ServiceCharge
import org.assertj.core.api.Assertions.*
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
internal class JpaServiceChargeRepositoryTest {
    @Autowired
    private lateinit var jpaServiceChargeRepository: JpaServiceChargeRepository

    @Test
    fun `should save ServiceCharge`() {
        val serviceCharge01 = ServiceCharge(
            id = UUID.randomUUID().toString(),
            status = FulfilmentStatus.REQUEST_SENT,
            amount = Money(Currency.getInstance("CNY"), BigDecimal("1000.00")),
            createdAt = OffsetDateTime.of(2022, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC),
            expiredAt = OffsetDateTime.of(2022, 1, 7, 3, 4, 5, 0, ZoneOffset.UTC),
            confirmedAt = null,
            paymentId = null,
            paymentMessage = null
        )
        val serviceCharge02 = ServiceCharge(
            id = UUID.randomUUID().toString(),
            status = FulfilmentStatus.REQUEST_CONFIRM_SUCCESS,
            amount = Money(Currency.getInstance("CNY"), BigDecimal("2000.00")),
            createdAt = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
            expiredAt = OffsetDateTime.of(2022, 2, 7, 3, 4, 5, 0, ZoneOffset.UTC),
            confirmedAt = OffsetDateTime.of(2022, 2, 3, 3, 4, 5, 0, ZoneOffset.UTC),
            paymentId = "2323412112243",
            paymentMessage = null,
        )
        val serviceCharge03 = ServiceCharge(
            id = UUID.randomUUID().toString(),
            status = FulfilmentStatus.REQUEST_CONFIRM_FAILED,
            amount = Money(Currency.getInstance("CNY"), BigDecimal("2000.00")),
            createdAt = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
            expiredAt = OffsetDateTime.of(2022, 2, 7, 3, 4, 5, 0, ZoneOffset.UTC),
            confirmedAt = OffsetDateTime.of(2022, 2, 3, 3, 4, 5, 0, ZoneOffset.UTC),
            paymentId = "2312112424534",
            paymentMessage = "Account doesn't have enough money to pay for the payment.",
        )

        val listOfServiceCharge = listOf(serviceCharge01, serviceCharge02, serviceCharge03)
        listOfServiceCharge.forEach { jpaServiceChargeRepository.save(it) }
        listOfServiceCharge.forEach {
            val serviceCharge = jpaServiceChargeRepository.findById(it.id)
            assertThat(serviceCharge).isNotNull
            assertThat(serviceCharge?.id).isEqualTo(it.id)
            assertThat(serviceCharge?.status).isEqualTo(it.status)
            assertThat(serviceCharge?.amount).isEqualTo(it.amount)
            assertThat(serviceCharge?.createdAt).isEqualTo(it.createdAt)
            assertThat(serviceCharge?.expiredAt).isEqualTo(it.expiredAt)
            assertThat(serviceCharge?.confirmedAt).isEqualTo(it.confirmedAt)
            assertThat(serviceCharge?.paymentId).isEqualTo(it.paymentId)
            assertThat(serviceCharge?.paymentMessage).isEqualTo(it.paymentMessage)
        }
    }
}