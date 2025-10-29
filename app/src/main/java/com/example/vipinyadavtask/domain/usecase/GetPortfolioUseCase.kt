package com.example.vipinyadavtask.domain.usecase

import com.example.vipinyadavtask.data.repository.HoldingsRepository
import com.example.vipinyadavtask.domain.model.Holding
import com.example.vipinyadavtask.domain.model.PortfolioCalculator
import com.example.vipinyadavtask.domain.model.PortfolioSummary

data class PortfolioResult(
    val holdings: List<Holding>,
    val summary: PortfolioSummary
)

class GetPortfolioUseCase(
    private val repository: HoldingsRepository
) {
    suspend fun execute(): Result<PortfolioResult> {
        return repository.getHoldings().map { holdings ->
            val summary = PortfolioCalculator.calculateSummary(holdings)
            PortfolioResult(holdings, summary)
        }
    }
}


