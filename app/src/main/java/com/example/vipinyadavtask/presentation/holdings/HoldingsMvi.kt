package com.example.vipinyadavtask.presentation.holdings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vipinyadavtask.domain.model.Holding
import com.example.vipinyadavtask.domain.model.PortfolioSummary
import com.example.vipinyadavtask.domain.usecase.GetPortfolioUseCase
import com.example.vipinyadavtask.data.remote.Injector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HoldingsIntent {
    data object Load : HoldingsIntent
    data object ToggleSummary : HoldingsIntent
}

data class HoldingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val holdings: List<Holding> = emptyList(),
    val summary: PortfolioSummary? = null,
    val isSummaryExpanded: Boolean = false
)

class HoldingsViewModel : ViewModel() {

    private val useCase: GetPortfolioUseCase = Injector.useCase

    private val _state = MutableStateFlow(HoldingsState(isLoading = true))
    val state: StateFlow<HoldingsState> = _state.asStateFlow()

    fun dispatch(intent: HoldingsIntent) {
        when (intent) {
            HoldingsIntent.Load -> load()
            HoldingsIntent.ToggleSummary -> toggleSummary()
        }
    }

    private fun load() {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            val result = useCase.execute()
            result.fold(
                onSuccess = { pr ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        holdings = pr.holdings,
                        summary = pr.summary
                    )
                },
                onFailure = { t ->
                    _state.value = _state.value.copy(isLoading = false, error = t.message ?: "Unknown error")
                }
            )
        }
    }

    private fun toggleSummary() {
        _state.value = _state.value.copy(isSummaryExpanded = !_state.value.isSummaryExpanded)
    }
}


