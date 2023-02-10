package com.thoughtworks.travels.adapters.secondary.rest

import com.thoughtworks.travels.application.PaymentService
import com.thoughtworks.travels.application.dto.PaymentRequest
import com.thoughtworks.travels.application.dto.PaymentResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class PaymentServiceAPIClient(
    private val paymentServiceWebClient: WebClient
) : PaymentService {
    override fun process(paymentRequest: PaymentRequest): PaymentResponse {
        try {
            val paymentResponse: Mono<PaymentResponse> = paymentServiceWebClient.post()
                .uri("/payments")
                .body(Mono.just(paymentRequest), PaymentRequest::class.java)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(PaymentResponse::class.java)
                .retry(3)
            val response = paymentResponse.blockOptional()
            if (response.isPresent) {
                return response.get()
            }
        } catch (e: Exception) {
            throw PaymentServiceException("Process payment: $paymentRequest failed: ${e.message}")
        }
        throw PaymentServiceException("Process payment: $paymentRequest failed: no response")
    }
}