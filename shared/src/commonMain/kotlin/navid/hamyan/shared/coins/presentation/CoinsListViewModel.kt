package navid.hamyan.shared.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import navid.hamyan.shared.coins.domain.GetCoinPriceHistoryUseCase
import navid.hamyan.shared.coins.domain.GetCoinsListUseCase
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.util.formatFiat
import navid.hamyan.shared.core.util.formatPercentage
import navid.hamyan.shared.core.util.toUiText

class CoinsListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
    private val getCoinPriceHistoryUseCase: GetCoinPriceHistoryUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsState())
    val state = _state
        .onStart { getAllCoins() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinsState(),
        )

    fun onCoinLongPressed(coinId: String) {
        _state.update {
            it.copy(chartState = ChartState(sparkLine = emptyList(), isLoading = true))
        }
        viewModelScope.launch {
            when (val result = getCoinPriceHistoryUseCase(coinId)) {
                is Result.Success -> _state.update { currentState ->
                    currentState.copy(
                        chartState = ChartState(
                            sparkLine = result.data.sortedBy { it.timestamp }.map { it.price },
                            isLoading = false,
                            coinName = currentState.coins.find { it.id == coinId }?.name.orEmpty(),
                        )
                    )
                }

                is Result.Error -> _state.update {
                    it.copy(
                        chartState = ChartState(
                            sparkLine = emptyList(),
                            isLoading = false,
                            coinName = "",
                        ),
                    )
                }
            }
        }
    }

    fun onChartDismissed() {
        _state.update {
            it.copy(chartState = null)
        }
    }

    private suspend fun getAllCoins() {
        when (val result = getCoinsListUseCase()) {
            is Result.Success -> _state.update {
                it.copy(
                    coins = result.data.map { coinItem ->
                        UiCoinListItem(
                            id = coinItem.coin.id,
                            name = coinItem.coin.name,
                            symbol = coinItem.coin.symbol,
                            iconUrl = coinItem.coin.iconUrl,
                            formattedPrice = formatFiat(coinItem.price),
                            formattedChange = formatPercentage(coinItem.change),
                            isPositive = coinItem.change >= 0,
                        )
                    }
                )
            }

            is Result.Error -> _state.update {
                it.copy(error = result.error.toUiText())
            }
        }
    }
}
