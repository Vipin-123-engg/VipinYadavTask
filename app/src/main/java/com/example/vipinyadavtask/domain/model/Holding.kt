package com.example.vipinyadavtask.domain.model

data class Holding(
    val symbol: String,
    val quantity: Int,
    val ltp: Double,
    val avgPrice: Double,
    val close: Double
)

data class PortfolioSummary(
    val currentValue: Double,
    val totalInvestment: Double,
    val totalPnl: Double,
    val todaysPnl: Double
)

object PortfolioCalculator {
    fun calculateSummary(holdings: List<Holding>): PortfolioSummary {
        var currentValue = 0.0
        var totalInvestment = 0.0
        var todaysPnl = 0.0
        for (h in holdings) {
            currentValue += h.ltp * h.quantity
            totalInvestment += h.avgPrice * h.quantity
            todaysPnl += (h.close - h.ltp) * h.quantity
        }
        val totalPnl = currentValue - totalInvestment
        return PortfolioSummary(
            currentValue = currentValue,
            totalInvestment = totalInvestment,
            totalPnl = totalPnl,
            todaysPnl = todaysPnl
        )
    }

    fun perHoldingPnl(holding: Holding): Double {
        return (holding.ltp - holding.avgPrice) * holding.quantity
    }
}






