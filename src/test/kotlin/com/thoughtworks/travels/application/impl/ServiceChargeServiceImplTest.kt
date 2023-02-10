package com.thoughtworks.travels.application.impl

import com.thoughtworks.travels.adapters.secondary.rest.PaymentServiceAPIClient
import com.thoughtworks.travels.adapters.secondary.rest.PaymentServiceException
import com.thoughtworks.travels.application.OffsetDateTimeProvider
import com.thoughtworks.travels.application.ServiceChargeService
import com.thoughtworks.travels.application.command.ConfirmServiceChargeCommand
import com.thoughtworks.travels.application.dto.PaymentRequest
import com.thoughtworks.travels.application.dto.PaymentResponse
import com.thoughtworks.travels.application.dto.PaymentStatus
import com.thoughtworks.travels.application.dto.ServiceChargeConfirmationDto
import com.thoughtworks.travels.application.exceptions.ServiceChargeException
import com.thoughtworks.travels.domain.model.FulfilmentStatus
import com.thoughtworks.travels.domain.model.Money
import com.thoughtworks.travels.domain.model.ServiceCharge
import com.thoughtworks.travels.domain.model.ServiceChargeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class ServiceChargeServiceImplTest {
    @Mock
    private lateinit var serviceChargeRepository: ServiceChargeRepository
    @Mock
    private lateinit var paymentServiceAPIClient: PaymentServiceAPIClient
    @Mock
    private lateinit var dateTimeProvider: OffsetDateTimeProvider

    private val serviceChargeService: ServiceChargeService by lazy {
        ServiceChargeServiceImpl(
            platformBankAccount,
            paymentIdentifierPrefix,
            serviceChargeRepository,
            paymentServiceAPIClient,
            dateTimeProvider,
        )
    }

    @Test
    fun `Given business account has 10000 CNY, When business need pay 10000 CNY, Then service charge confirmation success`() {
        val id = "10001"
        val amount = BigDecimal("10000")
        stubRepositoryReturnUnconfirmedServiceCharge(id, amount)
        whenever(
            paymentServiceAPIClient.process(
                PaymentRequest(
                    paymentIdentifier = "${paymentIdentifierPrefix}_$id",
                    amount = amount,
                    currency = "CNY",
                    fromBankAccount = customerBankAccount,
                    toBankAccount = platformBankAccount
                )
            )
        ).thenReturn(
            PaymentResponse(
                paymentId = "c26faa45-1a66-4f45-a42c-a31a2648baf7",
                paymentStatus = PaymentStatus.PAYMENT_SUCCESS,
                message = null,
            )
        )

        val confirmedAt = OffsetDateTime.of(2022, 2, 3, 3, 4, 5, 0, ZoneOffset.UTC)
        whenever(dateTimeProvider.now()).thenReturn(confirmedAt)

        val result: ServiceChargeConfirmationDto =
            serviceChargeService.confirmServiceCharge(ConfirmServiceChargeCommand(id, customerBankAccount))

        verify(
            serviceChargeRepository,
            times(1)
        ).save(
            ServiceCharge(
                id = id,
                paymentId = "c26faa45-1a66-4f45-a42c-a31a2648baf7",
                paymentMessage = null,
                status = FulfilmentStatus.REQUEST_CONFIRM_SUCCESS,
                amount = Money(Currency.getInstance("CNY"), amount),
                createdAt = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
                expiredAt = OffsetDateTime.of(2022, 2, 7, 3, 4, 5, 0, ZoneOffset.UTC),
                confirmedAt = confirmedAt
            )
        )

        assertThat(result).isEqualTo(
            ServiceChargeConfirmationDto(
                id = id,
                status = FulfilmentStatus.REQUEST_CONFIRM_SUCCESS.toString(),
                paymentId = "c26faa45-1a66-4f45-a42c-a31a2648baf7",
                message = null,
            )
        )
    }

    @Test
    fun `Given business account has 10000 CNY, When business need pay 10001 CNY, Then service charge confirmation failed`() {
        val id = "10002"
        val amount = BigDecimal("10001")
        stubRepositoryReturnUnconfirmedServiceCharge(id, amount)
        whenever(
            paymentServiceAPIClient.process(
                PaymentRequest(
                    paymentIdentifier = "${paymentIdentifierPrefix}_$id",
                    amount = amount,
                    currency = "CNY",
                    fromBankAccount = customerBankAccount,
                    toBankAccount = platformBankAccount
                )
            )
        ).thenReturn(
            PaymentResponse(
                paymentId = "c26faa45-1a66-4f45-a42c-a31a2648baf8",
                paymentStatus = PaymentStatus.PAYMENT_FAILED,
                message = "Account doesn't have enough money to pay for the payment.",
            )
        )

        val confirmedAt = OffsetDateTime.of(2022, 2, 3, 3, 4, 5, 0, ZoneOffset.UTC)
        whenever(dateTimeProvider.now()).thenReturn(confirmedAt)

        val result: ServiceChargeConfirmationDto =
            serviceChargeService.confirmServiceCharge(ConfirmServiceChargeCommand(id, customerBankAccount))

        verify(
            serviceChargeRepository,
            times(1)
        ).save(
            ServiceCharge(
                id = id,
                paymentId = "c26faa45-1a66-4f45-a42c-a31a2648baf8",
                paymentMessage = "Account doesn't have enough money to pay for the payment.",
                status = FulfilmentStatus.REQUEST_CONFIRM_FAILED,
                amount = Money(Currency.getInstance("CNY"), amount),
                createdAt = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
                expiredAt = OffsetDateTime.of(2022, 2, 7, 3, 4, 5, 0, ZoneOffset.UTC),
                confirmedAt = confirmedAt
            )
        )

        assertThat(result).isEqualTo(
            ServiceChargeConfirmationDto(
                id = id,
                status = FulfilmentStatus.REQUEST_CONFIRM_FAILED.toString(),
                paymentId = "c26faa45-1a66-4f45-a42c-a31a2648baf8",
                message = "Account doesn't have enough money to pay for the payment.",
            )
        )
    }

    @Test
    fun `Given ServiceCharge of Id 10001 does not exist, When business confirm this ServiceCharge, Then service charge confirmation failed`() {
        val id = "10003"
        val customerBankAccount = "4003830171874018"
        whenever(serviceChargeRepository.findById(id)).thenReturn(null)

        val exception = assertThrows<IllegalArgumentException> {
            serviceChargeService.confirmServiceCharge(ConfirmServiceChargeCommand(id, customerBankAccount))
        }
        assertThat(exception.message).isEqualTo("Service Charge Request doesn't exist.")
    }

    @Test
    fun `Given business account has 10000 CNY, Business need pay 10000 CNY, When payment service can not response, Then service charge confirmation failed`() {
        val id = "10004"
        val amount = BigDecimal("10000")
        stubRepositoryReturnUnconfirmedServiceCharge(id, amount)

        whenever(
            paymentServiceAPIClient.process(
                PaymentRequest(
                    paymentIdentifier = "${paymentIdentifierPrefix}_$id",
                    amount = amount,
                    currency = "CNY",
                    fromBankAccount = customerBankAccount,
                    toBankAccount = platformBankAccount
                )
            )
        ).thenThrow(PaymentServiceException("Process payment failed."))

        val exception = assertThrows<ServiceChargeException> {
            serviceChargeService.confirmServiceCharge(ConfirmServiceChargeCommand(id, customerBankAccount))
        }
        assertThat(exception.message).isEqualTo("Process payment error: Process payment failed.")
    }

    private fun stubRepositoryReturnUnconfirmedServiceCharge(id: String, amount: BigDecimal) {
        whenever(serviceChargeRepository.findById(id)).thenReturn(
            ServiceCharge(
                id = id,
                status = FulfilmentStatus.REQUEST_SENT,
                amount = Money(Currency.getInstance("CNY"), amount),
                createdAt = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
                expiredAt = OffsetDateTime.of(2022, 2, 7, 3, 4, 5, 0, ZoneOffset.UTC),
                confirmedAt = null,
                paymentId = null,
                paymentMessage = null,
            )
        )
    }

    companion object {
        private const val customerBankAccount: String = "4003830171874018"
        private const val platformBankAccount: String = "5496198584584769"
        private const val paymentIdentifierPrefix: String = "REN_NI_XIN_SERVICE_CHARGE_"
    }
}