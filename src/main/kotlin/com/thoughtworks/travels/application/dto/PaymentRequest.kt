package com.thoughtworks.travels.application.dto

import java.math.BigDecimal

data class PaymentRequest(
    val paymentIdentifier: String,
    val amount: BigDecimal,
    val currency: String,
    val fromBankAccount: String,
    val toBankAccount: String
)