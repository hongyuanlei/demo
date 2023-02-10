package com.thoughtworks.travels.application.impl

import com.thoughtworks.travels.adapters.secondary.rest.PaymentServiceException
import com.thoughtworks.travels.application.OffsetDateTimeProvider
import com.thoughtworks.travels.application.PaymentService
import com.thoughtworks.travels.application.ServiceChargeService
import com.thoughtworks.travels.application.command.ConfirmServiceChargeCommand
import com.thoughtworks.travels.application.dto.PaymentRequest
import com.thoughtworks.travels.application.dto.PaymentStatus
import com.thoughtworks.travels.application.dto.ServiceChargeConfirmationDto
import com.thoughtworks.travels.application.exceptions.ServiceChargeException
import com.thoughtworks.travels.domain.model.PaymentConfirmation
import com.thoughtworks.travels.domain.model.ServiceChargeRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ServiceChargeServiceImpl(
    @Value("\${platform.bankAccount}") private val platformBankAccount: String,
    @Value("\${platform.identifierPrefix.serviceCharge}") private val paymentIdentifierPrefix: String,
    private val serviceChargeRepository: ServiceChargeRepository,
    private val paymentService: PaymentService,
    private val dateTimeProvider: OffsetDateTimeProvider
) : ServiceChargeService {
    override fun confirmServiceCharge(confirmServiceChargeCommand: ConfirmServiceChargeCommand): ServiceChargeConfirmationDto {
        val id = confirmServiceChargeCommand.Id
        val serviceCharge =
            serviceChargeRepository.findById(id)
                ?: throw IllegalArgumentException("Service Charge Request doesn't exist.")
        val now = dateTimeProvider.now()

        try {
            val paymentResponse = paymentService.process(
                PaymentRequest(
                    paymentIdentifier = "${paymentIdentifierPrefix}_${id}",
                    amount = serviceCharge.amount.amount,
                    currency = serviceCharge.amount.currency.currencyCode,
                    fromBankAccount = confirmServiceChargeCommand.customerBankAccount,
                    toBankAccount = platformBankAccount
                )
            )

            serviceCharge.confirm(
                PaymentConfirmation(
                    paymentId = paymentResponse.paymentId,
                    confirmedAt = now,
                    isSuccess = paymentResponse.paymentStatus == PaymentStatus.PAYMENT_SUCCESS,
                    message = paymentResponse.message
                )
            )
            serviceChargeRepository.save(serviceCharge)

            return ServiceChargeConfirmationDto(
                id = id,
                paymentId = paymentResponse.paymentId,
                status = serviceCharge.status.toString(),
                message = paymentResponse.message,
            )
        } catch (e: PaymentServiceException) {
            throw ServiceChargeException("Process payment error: ${e.message}")
        } catch (e: Exception) {
            throw ServiceChargeException("Confirm ServiceCharge error: ${e.message}")
        }
    }
}