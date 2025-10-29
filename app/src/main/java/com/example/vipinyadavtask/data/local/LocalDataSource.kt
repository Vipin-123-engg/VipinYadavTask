package com.example.vipinyadavtask.data.local

import com.example.vipinyadavtask.data.local.db.HoldingEntity
import com.example.vipinyadavtask.data.local.db.HoldingsDao
import com.example.vipinyadavtask.domain.model.Holding

class LocalDataSource(
    private val dao: HoldingsDao
) {
    suspend fun getHoldings(): List<Holding> = dao.getAll().map { it.toDomain() }

    suspend fun replaceHoldings(items: List<Holding>) {
        val entities = items.map { it.toEntity() }
        dao.clear()
        dao.insertAll(entities)
    }

    private fun Holding.toEntity(): HoldingEntity = HoldingEntity(
        symbol = symbol,
        quantity = quantity,
        ltp = ltp,
        avgPrice = avgPrice,
        close = close
    )

    private fun HoldingEntity.toDomain(): Holding = Holding(
        symbol = symbol,
        quantity = quantity,
        ltp = ltp,
        avgPrice = avgPrice,
        close = close
    )
}


