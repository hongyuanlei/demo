package com.thoughtworks.travels.adapters.primary.controller

import com.thoughtworks.travels.application.SettlementInvoiceService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SettlementInvoiceController(private val settlementInvoiceService: SettlementInvoiceService) {
    @PostMapping(
        value = ["/corporate-travels/{id}/settlement-invoice/{sid}/confirmation"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun confirmation(@PathVariable("id") id: String, @PathVariable("sid") sid: String): ResponseEntity<Void> {
        return try {
            settlementInvoiceService.confirmation(sid)
            return ResponseEntity(HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: Exception) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}