package com.thoughtworks.travels.domain.model

import java.time.OffsetDateTime

class ServiceCharge(
    val id: String,
    var paymentId: String?,
    var paymentMessage: String?,
    var status: FulfilmentStatus,
    val amount: Money,
    val createdAt: OffsetDateTime,
    val expiredAt: OffsetDateTime,
    var confirmedAt: OffsetDateTime?
) {
    fun confirm(paymentConfirmation: PaymentConfirmation) {
        status =
            if (paymentConfirmation.isSuccess) FulfilmentStatus.REQUEST_CONFIRM_SUCCESS else FulfilmentStatus.REQUEST_CONFIRM_FAILED
        paymentId = paymentConfirmation.paymentId
        paymentMessage = paymentConfirmation.message
        confirmedAt = paymentConfirmation.confirmedAt
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ServiceCharge

        if (id != other.id) return false
        if (paymentId != other.paymentId) return false
        if (paymentMessage != other.paymentMessage) return false
        if (status != other.status) return false
        if (amount != other.amount) return false
        if (createdAt != other.createdAt) return false
        if (expiredAt != other.expiredAt) return false
        if (confirmedAt != other.confirmedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (paymentId?.hashCode() ?: 0)
        result = 31 * result + (paymentMessage?.hashCode() ?: 0)
        result = 31 * result + status.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + expiredAt.hashCode()
        result = 31 * result + (confirmedAt?.hashCode() ?: 0)
        return result
    }
}