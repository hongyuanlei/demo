package com.thoughtworks.travels.application.dto

data class ServiceChargeConfirmationDto(
    val id: String,
    val status: String,
    val paymentId: String,
    val message: String?,
)