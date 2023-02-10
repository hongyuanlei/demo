package com.thoughtworks.travels.adapters.primary.controller

import com.thoughtworks.travels.adapters.primary.controller.request.ServiceChargeConfirmationRequest
import com.thoughtworks.travels.adapters.primary.controller.response.ServiceChargeConfirmationResponse
import com.thoughtworks.travels.application.ServiceChargeService
import com.thoughtworks.travels.application.command.ConfirmServiceChargeCommand
import com.thoughtworks.travels.application.dto.ServiceChargeConfirmationDto
import com.thoughtworks.travels.application.exceptions.ServiceChargeException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class ServiceChargeController(private val serviceChargeService: ServiceChargeService) {
    @PostMapping(
        value = ["/corporate-travels/{id}/service-charge/confirmation"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun confirmation(
        @PathVariable("id") id: String,
        @RequestBody confirmServiceChargeRequest: ServiceChargeConfirmationRequest
    ): ResponseEntity<ServiceChargeConfirmationResponse> {
        return try {
            val serviceCharge = serviceChargeService.confirmServiceCharge(
                ConfirmServiceChargeCommand(
                    id,
                    confirmServiceChargeRequest.customerBankAccount
                )
            )
            return ResponseEntity(serviceCharge.toResponse(), HttpStatus.OK)
        } catch (e: IllegalArgumentException) {
            ResponseEntity(HttpStatus.BAD_REQUEST)
        } catch (e: ServiceChargeException) {
            ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    private fun ServiceChargeConfirmationDto.toResponse() = ServiceChargeConfirmationResponse(
        id = id,
        chargeStatus = status,
        paymentId = paymentId,
        message = message
    )
}