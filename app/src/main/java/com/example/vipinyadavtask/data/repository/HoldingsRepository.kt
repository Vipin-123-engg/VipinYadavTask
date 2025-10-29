package com.example.vipinyadavtask.data.repository

import com.example.vipinyadavtask.data.local.LocalDataSource
import com.example.vipinyadavtask.data.remote.RemoteDataSource
import com.example.vipinyadavtask.domain.model.Holding

interface HoldingsRepository {
    suspend fun getHoldings(): Result<List<Holding>>
}

class HoldingsRepositoryImpl(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource
) : HoldingsRepository {
    override suspend fun getHoldings(): Result<List<Holding>> {
        return runCatching {
            val fresh = remote.fetchHoldings()
            local.replaceHoldings(fresh)
            fresh
        }.recoverCatching {
            val cached = local.getHoldings()
            if (cached.isEmpty()) throw it
            cached
        }.fold(
            onSuccess = { Result.success(it) },
            onFailure = { Result.failure(it) }
        )
    }
}


