package com.thoughtworks.travels.domain.model

import java.time.OffsetDateTime

class SettlementInvoice(
    val id: String,
    val amount: Money,
    val billTo: InvoiceCompany,
    val invoiceDate: OffsetDateTime,
    val dueDate: OffsetDateTime,
    var status: FulfilmentStatus,
    val createdAt: OffsetDateTime,
    val expiredAt: OffsetDateTime,
    var confirmedAt: OffsetDateTime?
) {
    fun isExpiredAt(confirmedAt: OffsetDateTime): Boolean = confirmedAt.isAfter(expiredAt)

    fun confirmationFailed(confirmedAt: OffsetDateTime) {
        this.confirmedAt = confirmedAt
        this.status = FulfilmentStatus.REQUEST_CONFIRM_FAILED
    }

    fun confirmationSuccess(confirmedAt: OffsetDateTime) {
        this.confirmedAt = confirmedAt
        this.status = FulfilmentStatus.REQUEST_CONFIRM_SUCCESS
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SettlementInvoice

        if (id != other.id) return false
        if (amount != other.amount) return false
        if (billTo != other.billTo) return false
        if (invoiceDate != other.invoiceDate) return false
        if (dueDate != other.dueDate) return false
        if (status != other.status) return false
        if (createdAt != other.createdAt) return false
        if (expiredAt != other.expiredAt) return false
        if (confirmedAt != other.confirmedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + amount.hashCode()
        result = 31 * result + billTo.hashCode()
        result = 31 * result + invoiceDate.hashCode()
        result = 31 * result + dueDate.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + expiredAt.hashCode()
        result = 31 * result + (confirmedAt?.hashCode() ?: 0)
        return result
    }


}