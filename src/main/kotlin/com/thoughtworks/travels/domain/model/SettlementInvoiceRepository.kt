package com.thoughtworks.travels.domain.model

interface SettlementInvoiceRepository {
    fun nextIdentifier(): String
    fun findById(id: String): SettlementInvoice?
    fun save(settlementInvoice: SettlementInvoice)
}