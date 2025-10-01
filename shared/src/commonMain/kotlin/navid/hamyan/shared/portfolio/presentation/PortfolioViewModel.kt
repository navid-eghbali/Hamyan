package navid.hamyan.shared.portfolio.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.util.formatCoinUnit
import navid.hamyan.shared.core.util.formatFiat
import navid.hamyan.shared.core.util.formatPercentage
import navid.hamyan.shared.core.util.toUiText
import navid.hamyan.shared.portfolio.domain.PortfolioCoinModel
import navid.hamyan.shared.portfolio.domain.PortfolioRepository

class PortfolioViewModel(
    private val portfolioRepository: PortfolioRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(PortfolioState(isLoading = true))
    val state: StateFlow<PortfolioState> = combine(
        _state,
        portfolioRepository.allPortfolioCoinsFlow(),
        portfolioRepository.totalBalanceFlow(),
        portfolioRepository.cashBalanceFlow(),
    ) { currentState, allPortfolioCoins, totalBalance, cashBalance ->
        when (allPortfolioCoins) {
            is Result.Success -> handleSuccessState(
                currentState = currentState,
                portfolioCoins = allPortfolioCoins.data,
                totalBalanceResult = totalBalance,
                cashBalance = cashBalance,
            )

            is Result.Error -> handleErrorState(
                currentState = currentState,
                error = allPortfolioCoins.error,
            )
        }
    }.onStart {
        portfolioRepository.initializeBalance()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PortfolioState(isLoading = true),
    )

    private fun handleSuccessState(
        currentState: PortfolioState,
        portfolioCoins: List<PortfolioCoinModel>,
        totalBalanceResult: Result<Double, DataError>,
        cashBalance: Double,
    ): PortfolioState {
        val portfolioValue = when (totalBalanceResult) {
            is Result.Success -> formatFiat(totalBalanceResult.data)
            is Result.Error -> formatFiat(0.0)
        }

        return currentState.copy(
            coins = portfolioCoins.map { it.toUiPortfolioCoinItem() },
            portfolioValue = portfolioValue,
            cashBalance = formatFiat(cashBalance),
            showBuyButton = portfolioCoins.isNotEmpty(),
            isLoading = false,
        )
    }

    private fun handleErrorState(
        currentState: PortfolioState,
        error: DataError,
    ): PortfolioState = currentState.copy(
        isLoading = false,
        error = error.toUiText(),
    )

    private fun PortfolioCoinModel.toUiPortfolioCoinItem(): UiPortfolioCoinItem = UiPortfolioCoinItem(
        id = coin.id,
        name = coin.name,
        iconUrl = coin.iconUrl,
        amountInUnitText = formatCoinUnit(ownedAmountInUnit, coin.symbol),
        amountInFiatText = formatFiat(ownedAmountInFiat),
        performancePercentText = formatPercentage(performancePercent),
        isPositive = performancePercent >= 0,
    )

}
