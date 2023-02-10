package com.thoughtworks.travels.adapters.primary.controller

import com.thoughtworks.travels.application.ServiceChargeService
import com.thoughtworks.travels.application.command.ConfirmServiceChargeCommand
import com.thoughtworks.travels.application.dto.ServiceChargeConfirmationDto
import com.thoughtworks.travels.application.exceptions.ServiceChargeException
import com.thoughtworks.travels.domain.model.FulfilmentStatus
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [ServiceChargeController::class])
internal class ServiceChargeControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var serviceChargeServiceStub: ServiceChargeService

    @Test
    fun `should confirm service charge successful`() {
        val contractId = "10001"
        val customerBankAccount = "83820283192"
        val confirmServiceChargeCommand = ConfirmServiceChargeCommand(contractId, customerBankAccount)
        val serviceChargeConfirmationDto = ServiceChargeConfirmationDto(
            id = contractId,
            paymentId = "c26faa45-1a66-4f45-a42c-a31a2648baf7",
            status = FulfilmentStatus.REQUEST_CONFIRM_SUCCESS.toString(),
            message = null
        )
        whenever(serviceChargeServiceStub.confirmServiceCharge(confirmServiceChargeCommand)).thenReturn(
            serviceChargeConfirmationDto
        )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/corporate-travels/${contractId}/service-charge/confirmation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"customerBankAccount\": \"${customerBankAccount}\"}")
        )
            .andExpect(status().isOk)
            .andExpect(content().json(serviceChargeConfirmationDto.toJson()))
    }

    @Test
    fun `should return BadRequest when serviceChargeService throw IllegalArgumentException`() {
        val contractId = "10001"
        val customerBankAccount = "83820283192"
        val confirmServiceChargeCommand = ConfirmServiceChargeCommand(contractId, customerBankAccount)
        whenever(serviceChargeServiceStub.confirmServiceCharge(confirmServiceChargeCommand)).thenThrow(
            IllegalArgumentException("Service Charge Request doesn't exist.")
        )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/corporate-travels/${contractId}/service-charge/confirmation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"customerBankAccount\": \"${customerBankAccount}\"}")
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should return InternalServerError when serviceChargeService throw ServiceChargeException`() {
        val contractId = "10001"
        val customerBankAccount = "83820283192"
        val confirmServiceChargeCommand = ConfirmServiceChargeCommand(contractId, customerBankAccount)
        whenever(serviceChargeServiceStub.confirmServiceCharge(confirmServiceChargeCommand)).thenThrow(
            ServiceChargeException("Account doesn't have enough money to pay for the payment.")
        )
        mockMvc.perform(
            MockMvcRequestBuilders.post("/corporate-travels/${contractId}/service-charge/confirmation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .content("{\"customerBankAccount\": \"${customerBankAccount}\"}")
        )
            .andExpect(status().isInternalServerError)

    }

    companion object {
        private fun ServiceChargeConfirmationDto.toJson(): String = """
            {
                "id": "$id",
                "paymentId": "$paymentId",
                "chargeStatus": "$status",
                "message": null
            }
        """.trimIndent()
    }
}