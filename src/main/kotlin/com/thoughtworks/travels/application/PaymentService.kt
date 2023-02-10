package com.thoughtworks.travels.application

import com.thoughtworks.travels.application.dto.PaymentRequest
import com.thoughtworks.travels.application.dto.PaymentResponse

interface PaymentService {
    fun process(paymentRequest: PaymentRequest): PaymentResponse
}