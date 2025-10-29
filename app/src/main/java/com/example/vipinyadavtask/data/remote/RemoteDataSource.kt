package com.example.vipinyadavtask.data.remote

import com.example.vipinyadavtask.domain.model.Holding

class RemoteDataSource(
    private val api: HoldingsApi
) {
    suspend fun fetchHoldings(): List<Holding> {
        val response = api.getHoldings()
        return response.data.userHolding.map { dto ->
            Holding(
                symbol = dto.symbol,
                quantity = dto.quantity,
                ltp = dto.ltp,
                avgPrice = dto.avgPrice,
                close = dto.close
            )
        }
    }
}


