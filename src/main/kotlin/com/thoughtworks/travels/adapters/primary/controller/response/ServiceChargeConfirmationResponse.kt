package com.thoughtworks.travels.adapters.primary.controller.response

data class ServiceChargeConfirmationResponse(
    val id: String,
    val chargeStatus: String,
    val paymentId: String,
    val message: String?,
)