package com.thoughtworks.travels.application

import com.thoughtworks.travels.application.command.ConfirmServiceChargeCommand
import com.thoughtworks.travels.application.dto.ServiceChargeConfirmationDto

interface ServiceChargeService {
    fun confirmServiceCharge(confirmServiceChargeCommand: ConfirmServiceChargeCommand): ServiceChargeConfirmationDto
}