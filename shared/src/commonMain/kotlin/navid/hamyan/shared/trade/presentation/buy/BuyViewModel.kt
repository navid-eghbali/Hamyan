package navid.hamyan.shared.trade.presentation.buy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import navid.hamyan.shared.coins.domain.GetCoinDetailsUseCase
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.util.formatFiat
import navid.hamyan.shared.core.util.toUiText
import navid.hamyan.shared.portfolio.domain.PortfolioRepository
import navid.hamyan.shared.trade.domain.BuyCoinUseCase
import navid.hamyan.shared.trade.presentation.common.TradeState
import navid.hamyan.shared.trade.presentation.common.UiTradeCoinItem
import navid.hamyan.shared.trade.presentation.mapper.toCoin

class BuyViewModel(
    private val coinId: String,
    private val portfolioRepository: PortfolioRepository,
    private val getCoinDetailsUseCase: GetCoinDetailsUseCase,
    private val buyCoinUseCase: BuyCoinUseCase,
) : ViewModel() {

    private val _amount = MutableStateFlow("")
    private val _state = MutableStateFlow(TradeState())
    val state = combine(
        _state,
        _amount
    ) { state, amount ->
        state.copy(amount = amount)
    }.onStart {
        val balance = portfolioRepository.cashBalanceFlow().first()
        getCoinDetails(balance)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = TradeState(isLoading = true),
    )
    private val _events = Channel<BuyEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun onAmountChanged(amount: String) {
        _amount.update { amount }
    }

    fun onBuyClicked() {
        val tradeCoin = _state.value.coin ?: return
        viewModelScope.launch {
            when (val result = buyCoinUseCase(coin = tradeCoin.toCoin(), amountInFiat = _amount.value.toDouble(), price = tradeCoin.price)) {
                is Result.Success -> _events.trySend(BuyEvent.BuySuccess)
                is Result.Error -> _state.update { it.copy(isLoading = false, error = result.error.toUiText()) }
            }
        }
    }

    private suspend fun getCoinDetails(balance: Double) {
        when (val coinDetails = getCoinDetailsUseCase(coinId)) {
            is Result.Success -> _state.update {
                it.copy(
                    isLoading = false,
                    coin = UiTradeCoinItem(
                        id = coinDetails.data.coin.id,
                        name = coinDetails.data.coin.name,
                        symbol = coinDetails.data.coin.symbol,
                        iconUrl = coinDetails.data.coin.iconUrl,
                        price = coinDetails.data.price,
                    ),
                    availableAmount = "Available: ${formatFiat(balance)}",
                )
            }

            is Result.Error -> _state.update {
                it.copy(
                    isLoading = false,
                    error = coinDetails.error.toUiText(),
                )
            }
        }
    }

    sealed interface BuyEvent {
        data object BuySuccess : BuyEvent
    }
}
