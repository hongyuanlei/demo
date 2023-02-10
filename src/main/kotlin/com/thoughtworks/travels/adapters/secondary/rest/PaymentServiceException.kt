package com.thoughtworks.travels.adapters.secondary.rest

class PaymentServiceException(override val message: String): RuntimeException(message)