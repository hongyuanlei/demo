package com.thoughtworks.travels.adapters.primary.controller

import com.thoughtworks.travels.application.SettlementInvoiceService
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.*

@WebMvcTest(controllers = [SettlementInvoiceController::class])
internal class SettlementInvoiceControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var settlementInvoiceService: SettlementInvoiceService

    @Test
    fun `should confirm settlement invoice successful`() {
        mockMvc.perform(
            MockMvcRequestBuilders.post("/corporate-travels/${contractId}/settlement-invoice/${settlementInvoiceId}/confirmation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun `should return BadRequest when SettlementInvoiceService throw IllegalArgumentException`() {
        whenever(settlementInvoiceService.confirmation(settlementInvoiceId)).thenThrow(IllegalArgumentException("Settlement Invoice Request doesn't exist."))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/corporate-travels/${contractId}/settlement-invoice/${settlementInvoiceId}/confirmation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)

    }

    @Test
    fun `should return InternalServerError when SettlementInvoiceService throw Exception`() {
        whenever(settlementInvoiceService.confirmation(settlementInvoiceId)).thenThrow(RuntimeException("Unknown exception."))
        mockMvc.perform(
            MockMvcRequestBuilders.post("/corporate-travels/${contractId}/settlement-invoice/${settlementInvoiceId}/confirmation")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
        )
            .andExpect(MockMvcResultMatchers.status().isInternalServerError)
    }

    companion object {
        private val contractId = UUID.randomUUID().toString()
        private val settlementInvoiceId = UUID.randomUUID().toString()
    }
}