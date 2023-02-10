package com.thoughtworks.travels.adapters.secondary.persistence

import com.thoughtworks.travels.domain.model.Money
import com.thoughtworks.travels.domain.model.ServiceCharge
import com.thoughtworks.travels.domain.model.ServiceChargeRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JpaServiceChargeRepository(private val japServiceChargeRepositoryInterface: JpaServiceChargeRepositoryInterface) :
    ServiceChargeRepository {
    override fun nextIdentifier(): String {
        return UUID.randomUUID().toString()
    }

    override fun findById(id: String): ServiceCharge? {
        val entity = japServiceChargeRepositoryInterface.findById(id)
        if (entity.isPresent) {
            return entity.get().toModel()
        }
        return null;
    }

    override fun save(serviceCharge: ServiceCharge) {
        japServiceChargeRepositoryInterface.save(serviceCharge.toEntity())
    }

    private fun ServiceCharge.toEntity() = ServiceChargeEntity(
        id = id,
        chargeAmount = amount.amount,
        chargeCurrency = amount.currency.currencyCode,
        status = status,
        createdAt = createdAt,
        expiredAt = expiredAt,
        confirmedAt = confirmedAt,
        paymentId = paymentId,
        paymentMessage = paymentMessage
    )

    private fun ServiceChargeEntity.toModel() = ServiceCharge(
        id = id,
        amount = Money(Currency.getInstance(chargeCurrency), chargeAmount),
        status = status,
        createdAt = createdAt,
        expiredAt = expiredAt,
        confirmedAt = confirmedAt,
        paymentId = paymentId,
        paymentMessage = paymentMessage
    )
}

@Repository
interface JpaServiceChargeRepositoryInterface : JpaRepository<ServiceChargeEntity, String>