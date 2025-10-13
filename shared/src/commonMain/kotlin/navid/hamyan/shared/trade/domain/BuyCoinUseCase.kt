package navid.hamyan.shared.trade.domain

import kotlinx.coroutines.flow.first
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.EmptyResult
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.domain.coin.Coin
import navid.hamyan.shared.portfolio.domain.PortfolioCoinModel
import navid.hamyan.shared.portfolio.domain.PortfolioRepository

class BuyCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
) {
    suspend operator fun invoke(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val balance = portfolioRepository.cashBalanceFlow().first()
        if (balance < amountInFiat) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }

        val existingCoin = when (val result = portfolioRepository.getPortfolioCoin(coin.id)) {
            is Result.Success -> result.data
            is Result.Error -> return Result.Error(result.error)
        }
        val amountInUnit = amountInFiat / price
        if (existingCoin != null) {
            val newAmountOwned = existingCoin.ownedAmountInUnit + amountInUnit
            val newTotalInvestment = existingCoin.ownedAmountInFiat + amountInFiat
            val newAveragePurchasePrice = newTotalInvestment / newAmountOwned
            portfolioRepository.savePortfolioCoin(
                portfolioCoin = existingCoin.copy(
                    averagePurchasePrice = newAveragePurchasePrice,
                    ownedAmountInUnit = newAmountOwned,
                    ownedAmountInFiat = newTotalInvestment,
                )
            )
        } else {
            portfolioRepository.savePortfolioCoin(
                portfolioCoin = PortfolioCoinModel(
                    coin = coin,
                    performancePercent = 00.0,
                    averagePurchasePrice = price,
                    ownedAmountInUnit = amountInUnit,
                    ownedAmountInFiat = amountInFiat,
                )
            )
        }
        portfolioRepository.updateCashBalance(balance - amountInFiat)
        return Result.Success(Unit)
    }
}
