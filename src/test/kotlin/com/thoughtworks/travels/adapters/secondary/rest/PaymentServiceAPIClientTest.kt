package com.thoughtworks.travels.adapters.secondary.rest

import com.thoughtworks.travels.application.dto.PaymentRequest
import com.thoughtworks.travels.application.dto.PaymentStatus
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.math.BigDecimal

@Tag("component")
@SpringBootTest
internal class PaymentServiceAPIClientTest {
    @Autowired
    private lateinit var paymentServiceAPIClient: PaymentServiceAPIClient

    @Test
    fun `given account has 10000 CNY, payment should success when payment amount is 10000 CNY`() {
        val paymentRequest =
            PaymentRequest(
                paymentIdentifier = "REN_NI_XIN_SERVICE_CHARGE_100001",
                amount =  BigDecimal("10000.00"),
                currency = "CNY",
                fromBankAccount = "4003830171874018",
                toBankAccount = "5496198584584769"
            )
        val response = paymentServiceAPIClient.process(paymentRequest)
        assertThat(response.paymentId).isEqualTo("c26faa45-1a66-4f45-a42c-a31a2648baf7")
        assertThat(response.paymentStatus).isEqualTo(PaymentStatus.PAYMENT_SUCCESS)
    }

    @Test
    fun `given account has 10000 CNY, payment should failed when payment amount is 10001 CNY`() {
        val paymentRequest =
            PaymentRequest(
                paymentIdentifier = "REN_NI_XIN_SERVICE_CHARGE_100002",
                amount =  BigDecimal("10001.00"),
                currency = "CNY",
                fromBankAccount = "4003830171874018",
                toBankAccount = "5496198584584769"
            )
        val response = paymentServiceAPIClient.process(paymentRequest)
        assertThat(response.paymentId).isEqualTo("c26faa45-1a66-4f45-a42c-a31a2648baf8")
        assertThat(response.paymentStatus).isEqualTo(PaymentStatus.PAYMENT_FAILED)
        assertThat(response.message).isEqualTo("Account doesn't have enough money to pay for the payment.")
    }

    @Test
    fun `should throw error when 3rd party payment service can't response within 10 seconds`() {
        val paymentRequest =
            PaymentRequest(
                paymentIdentifier = "REN_NI_XIN_SERVICE_CHARGE_100003",
                amount =  BigDecimal("10000.00"),
                currency = "CNY",
                fromBankAccount = "4003830171874018",
                toBankAccount = "5496198584584769"
            )
        val exception = assertThrows<PaymentServiceException> {
            paymentServiceAPIClient.process(paymentRequest)
        }
        assertThat(exception.message).isEqualTo("Process payment: $paymentRequest failed: nested exception is io.netty.handler.timeout.ReadTimeoutException")
    }
}