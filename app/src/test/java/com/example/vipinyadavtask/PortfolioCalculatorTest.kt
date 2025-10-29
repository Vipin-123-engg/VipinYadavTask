package com.example.vipinyadavtask

import com.example.vipinyadavtask.domain.model.Holding
import com.example.vipinyadavtask.domain.model.PortfolioCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class PortfolioCalculatorTest {
    @Test
    fun `summary calculations are correct`() {
        val holdings = listOf(
            Holding("A", 2, ltp = 10.0, avgPrice = 8.0, close = 9.0),
            Holding("B", 3, ltp = 20.0, avgPrice = 25.0, close = 30.0)
        )

        val summary = PortfolioCalculator.calculateSummary(holdings)

        assertEquals(2 * 10.0 + 3 * 20.0, summary.currentValue, 0.001)
        assertEquals(2 * 8.0 + 3 * 25.0, summary.totalInvestment, 0.001)
        assertEquals(summary.currentValue - summary.totalInvestment, summary.totalPnl, 0.001)
        assertEquals((9.0 - 10.0) * 2 + (30.0 - 20.0) * 3, summary.todaysPnl, 0.001)
    }
}






