package com.example.vipinyadavtask

import com.example.vipinyadavtask.data.repository.HoldingsRepository
import com.example.vipinyadavtask.domain.model.Holding
import com.example.vipinyadavtask.domain.usecase.GetPortfolioUseCase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetPortfolioUseCaseTest {
    @Test
    fun `maps repository holdings to portfolio result with summary`() = runBlocking {
        val holdings = listOf(
            Holding("A", 2, ltp = 10.0, avgPrice = 8.0, close = 9.0),
            Holding("B", 3, ltp = 20.0, avgPrice = 25.0, close = 30.0)
        )
        val repo = object : HoldingsRepository {
            override suspend fun getHoldings(): Result<List<Holding>> = Result.success(holdings)
        }

        val useCase = GetPortfolioUseCase(repo)
        val result = useCase.execute().getOrThrow()

        assertEquals(holdings, result.holdings)
        // smoke-check summary math
        assertEquals(2 * 10.0 + 3 * 20.0, result.summary.currentValue, 0.001)
        assertEquals(2 * 8.0 + 3 * 25.0, result.summary.totalInvestment, 0.001)
    }
}


