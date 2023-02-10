package com.thoughtworks.travels.domain.model

interface ServiceChargeRepository {
    fun nextIdentifier(): String
    fun findById(id: String): ServiceCharge?
    fun save(serviceCharge: ServiceCharge)
}