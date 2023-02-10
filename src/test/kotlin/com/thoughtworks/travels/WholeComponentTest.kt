package com.thoughtworks.travels

import com.thoughtworks.travels.adapters.primary.controller.request.ServiceChargeConfirmationRequest
import com.thoughtworks.travels.adapters.primary.controller.response.ServiceChargeConfirmationResponse
import com.thoughtworks.travels.adapters.secondary.persistence.JpaServiceChargeRepository
import com.thoughtworks.travels.domain.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.math.BigDecimal
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

@Tag("component")
@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
class WholeComponentTest {
    private lateinit var webTestClient: WebTestClient

    private lateinit var httpClient: HttpClient

    @Autowired
    private lateinit var serviceChargeRepository: JpaServiceChargeRepository
    @Autowired
    private lateinit var settlementInvoiceRepository: SettlementInvoiceRepository

    @BeforeEach
    fun setUp() {

        webTestClient =
            WebTestClient
                .bindToServer()
                .baseUrl("http://localhost:8080")
                .responseTimeout(Duration.ofSeconds(15))
                .build()
    }

    @Test
    fun `service charge confirmation`() {
        val id = "100001"
        saveUnconfirmedServiceCharge(id)
        webTestClient.post()
            .uri("/corporate-travels/${id}/service-charge/confirmation")

            .contentType(MediaType.APPLICATION_JSON)
            .body(
                Mono.just(
                    ServiceChargeConfirmationRequest("4003830171874018")
                ),
                ServiceChargeConfirmationRequest::class.java
            )
            .exchange()
            .expectStatus().isOk
            .expectBody().equals("{\"id\":\"100001\", \"chargeStatus\": \"REQUEST_CONFIRM_SUCCESS\", \"paymentId\": \"c26faa45-1a66-4f45-a42c-a31a2648baf7\", \"message\": null}")
    }

    @Test
    fun `Settlement invoice confirmation`() {
        val id = UUID.randomUUID().toString()
        saveUnconfirmedSettlementInvoice(id)
        webTestClient.post()
            .uri("/corporate-travels/100001/settlement-invoice/${id}/confirmation")
            .contentType(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
    }

    private fun saveUnconfirmedServiceCharge(id: String) {
        serviceChargeRepository.save(
            ServiceCharge(
                id = id,
                status = FulfilmentStatus.REQUEST_SENT,
                amount = Money(Currency.getInstance("CNY"), BigDecimal("10000.00")),
                createdAt = OffsetDateTime.of(2022, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC),
                expiredAt = OffsetDateTime.of(2022, 1, 7, 3, 4, 5, 0, ZoneOffset.UTC),
                confirmedAt = null,
                paymentId = null,
                paymentMessage = null
            )
        )
    }

    private fun saveUnconfirmedSettlementInvoice(id: String) {
        settlementInvoiceRepository.save(
            SettlementInvoice(
                id = id,
                amount = Money(Currency.getInstance("CNY"), BigDecimal("1000.00")),
                billTo = InvoiceCompany(
                    name = "Smith Enterprises",
                    address = "456 Sierra Dr Mountain View 94039",
                ),
                invoiceDate = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
                dueDate = OffsetDateTime.of(2022, 3, 2, 3, 4, 5, 0, ZoneOffset.UTC),
                status = FulfilmentStatus.REQUEST_SENT,
                createdAt = OffsetDateTime.of(2022, 2, 2, 3, 4, 5, 0, ZoneOffset.UTC),
                expiredAt = OffsetDateTime.of(2022, 2, 7, 3, 4, 5, 0, ZoneOffset.UTC),
                confirmedAt = null
            )
        )
    }
}