package com.thoughtworks.travels.application.dto

data class PaymentResponse(val paymentId: String, val paymentStatus: PaymentStatus, val message: String?)
