package com.thoughtworks.travels.domain.model

import java.time.OffsetDateTime

data class PaymentConfirmation(val paymentId: String, val confirmedAt: OffsetDateTime, val isSuccess: Boolean, val message: String?)
