package com.thoughtworks.travels.adapters.secondary.persistence

import com.thoughtworks.travels.domain.model.InvoiceCompany
import com.thoughtworks.travels.domain.model.Money
import com.thoughtworks.travels.domain.model.SettlementInvoice
import com.thoughtworks.travels.domain.model.SettlementInvoiceRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class JpaSettlementInvoiceRepository(
    private val jpaSettlementInvoiceRepositoryInterface: JpaSettlementInvoiceRepositoryInterface
): SettlementInvoiceRepository {
    override fun nextIdentifier(): String {
        return UUID.randomUUID().toString()
    }

    override fun findById(id: String): SettlementInvoice? {
        val entity = jpaSettlementInvoiceRepositoryInterface.findById(id)
        if (entity.isPresent) {
            return entity.get().toModel()
        }
        return null;
    }

    override fun save(settlementInvoice: SettlementInvoice) {
        jpaSettlementInvoiceRepositoryInterface.save(settlementInvoice.toEntity())
    }

    private fun SettlementInvoice.toEntity() = SettlementInvoiceEntity(
        id = id,
        amount = amount.amount,
        currency = amount.currency.toString(),
        invoiceDate = invoiceDate,
        dueDate = dueDate,
        companyName = billTo.name,
        companyAddress = billTo.address,
        status = status,
        createdAt = createdAt,
        expiredAt = expiredAt,
        confirmedAt = confirmedAt
    )

    private fun SettlementInvoiceEntity.toModel() = SettlementInvoice(
        id = id,
        amount = Money(Currency.getInstance(currency), amount),
        billTo = InvoiceCompany(
            name = companyName,
            address = companyAddress,
        ),
        invoiceDate = invoiceDate,
        dueDate = dueDate,
        status = status,
        createdAt = createdAt,
        expiredAt = expiredAt,
        confirmedAt = confirmedAt
    )
}

@Repository
interface JpaSettlementInvoiceRepositoryInterface : JpaRepository<SettlementInvoiceEntity, String>