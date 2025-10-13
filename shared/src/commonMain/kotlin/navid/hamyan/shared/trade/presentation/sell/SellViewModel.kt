package navid.hamyan.shared.trade.presentation.sell

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import navid.hamyan.shared.coins.domain.GetCoinDetailsUseCase
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.util.formatFiat
import navid.hamyan.shared.core.util.toUiText
import navid.hamyan.shared.portfolio.domain.PortfolioRepository
import navid.hamyan.shared.trade.domain.SellCoinUseCase
import navid.hamyan.shared.trade.presentation.common.TradeState
import navid.hamyan.shared.trade.presentation.common.UiTradeCoinItem
import navid.hamyan.shared.trade.presentation.mapper.toCoin

class SellViewModel(
    private val portfolioRepository: PortfolioRepository,
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val sellCoinUseCase: SellCoinUseCase,
) : ViewModel() {

    private val tempCoinId = "1"
    private val _amount = MutableStateFlow("")
    private val _state = MutableStateFlow(TradeState())
    val state = combine(
        _state,
        _amount
    ) { state, amount ->
        state.copy(amount = amount)
    }.onStart {
        when (val result = portfolioRepository.getPortfolioCoin(tempCoinId)) {
            is Result.Success -> {
                result.data?.ownedAmountInUnit?.let {
                    getCoinDetails(it)
                }
            }

            is Result.Error -> _state.update {
                it.copy(
                    isLoading = false,
                    error = result.error.toUiText()
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TradeState(isLoading = true),
    )

    fun onAmountChanged(amount: String) {
        _amount.update { amount }
    }

    fun onSellClicked() {
        val tradeCoin = _state.value.coin ?: return
        viewModelScope.launch {
            when (val result = sellCoinUseCase(coin = tradeCoin.toCoin(), amountInFiat = _amount.value.toDouble(), price = tradeCoin.price)) {
                is Result.Success -> TODO()
                is Result.Error -> _state.update { it.copy(isLoading = false, error = result.error.toUiText()) }
            }
        }
    }

    private suspend fun getCoinDetails(ownedAmountInUnit: Double) {
        when (val result = getCoinDetailsUseCase(tempCoinId)) {
            is Result.Success -> {
                val availableAmountInFiat = result.data.price * ownedAmountInUnit
                _state.update {
                    it.copy(
                        isLoading = false,
                        coin = UiTradeCoinItem(
                            id = result.data.coin.id,
                            name = result.data.coin.name,
                            symbol = result.data.coin.symbol,
                            iconUrl = result.data.coin.iconUrl,
                            price = result.data.price,
                        ),
                        availableAmount = "Available: ${formatFiat(availableAmountInFiat)}",
                    )
                }
            }

            is Result.Error -> _state.update {
                it.copy(
                    isLoading = false,
                    error = result.error.toUiText()
                )
            }
        }
    }
}
