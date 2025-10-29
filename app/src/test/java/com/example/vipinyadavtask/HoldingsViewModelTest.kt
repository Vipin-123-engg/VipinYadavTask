package com.example.vipinyadavtask

import com.example.vipinyadavtask.data.repository.HoldingsRepository
import com.example.vipinyadavtask.domain.model.Holding
import com.example.vipinyadavtask.domain.usecase.GetPortfolioUseCase
import com.example.vipinyadavtask.presentation.holdings.HoldingsIntent
import com.example.vipinyadavtask.presentation.holdings.HoldingsViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HoldingsViewModelTest {
    @Test
    fun `toggle summary flips flag`() = runBlocking {
        val holdings = listOf(Holding("A", 1, 1.0, 1.0, 1.0))
        val repo = object : HoldingsRepository {
            override suspend fun getHoldings() = Result.success(holdings)
        }
        val vm = HoldingsViewModel(GetPortfolioUseCase(repo))
        // load
        vm.dispatch(HoldingsIntent.Load)
        // wait until not loading
        val first = vm.state.first { !it.isLoading }
        assertFalse(first.isSummaryExpanded)
        vm.dispatch(HoldingsIntent.ToggleSummary)
        val second = vm.state.first()
        assertTrue(second.isSummaryExpanded)
    }
}


