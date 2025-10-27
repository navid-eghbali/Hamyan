package navid.hamyan.shared.portfolio.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.EmptyResult
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.domain.coin.Coin
import navid.hamyan.shared.portfolio.domain.PortfolioCoinModel
import navid.hamyan.shared.portfolio.domain.PortfolioRepository

class PortfolioRepositoryFake : PortfolioRepository {

    private val _data = MutableStateFlow<Result<List<PortfolioCoinModel>, DataError.Remote>>(
        Result.Success(emptyList())
    )
    private val _cashBalance = MutableStateFlow(CASH_BALANCE)
    private val _portfolioValue = MutableStateFlow(PORTFOLIO_VALUE)

    private val listOfCoins = mutableListOf<PortfolioCoinModel>()

    override suspend fun initializeBalance() {
        // No-op
    }

    override fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> = _data.asStateFlow()

    override suspend fun getPortfolioCoin(coinId: String): Result<PortfolioCoinModel?, DataError.Remote> =
        Result.Success(PORTFOLIO_COIN)

    override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> {
        listOfCoins.add(portfolioCoin)
        _portfolioValue.update { listOfCoins.sumOf { it.ownedAmountInFiat } }
        _data.update { Result.Success(listOfCoins) }
        return Result.Success(Unit)
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        _data.update { Result.Success(emptyList()) }
    }

    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> =
        _portfolioValue.map { Result.Success(it) }

    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> =
        _cashBalance.combine(_portfolioValue) { cash, portfolioValue ->
            cash + portfolioValue
        }.map { Result.Success(it) }

    override fun cashBalanceFlow(): Flow<Double> = _cashBalance.asStateFlow()

    override suspend fun updateCashBalance(newBalance: Double) {
        _cashBalance.update { newBalance }
    }

    fun simulateError() {
        _data.update { Result.Error(DataError.Remote.SERVER) }
    }

    companion object {
        val FAKE_COIN = Coin(
            id = "id",
            name = "name",
            symbol = "symbol",
            iconUrl = "icon-url",
        )
        val PORTFOLIO_COIN = PortfolioCoinModel(
            coin = FAKE_COIN,
            performancePercent = 10.0,
            averagePurchasePrice = 10.0,
            ownedAmountInUnit = 1000.0,
            ownedAmountInFiat = 3000.0,
        )
        const val CASH_BALANCE = 10000.0
        const val PORTFOLIO_VALUE = 0.0
    }
}
