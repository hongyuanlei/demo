package com.thoughtworks.travels.application.dto

import java.math.BigDecimal
import java.time.OffsetDateTime

data class InvoiceMessage(
    val invoiceIdentifier: String,
    val invoiceDate: OffsetDateTime,
    val dueDate: OffsetDateTime,
    val amount: BigDecimal,
    val currency: String,
    val companyName: String,
    val companyAddress: String,
)