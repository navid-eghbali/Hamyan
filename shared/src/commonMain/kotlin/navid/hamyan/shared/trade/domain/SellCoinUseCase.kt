package navid.hamyan.shared.trade.domain

import kotlinx.coroutines.flow.first
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.EmptyResult
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.domain.coin.Coin
import navid.hamyan.shared.portfolio.domain.PortfolioRepository

class SellCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
) {
    suspend operator fun invoke(
        coin: Coin,
        amountInFiat: Double,
        price: Double,
    ): EmptyResult<DataError> {
        val sellAllThreshold = 1
        when (val existingCoinResult = portfolioRepository.getPortfolioCoin(coin.id)) {
            is Result.Success -> {
                val existingCoin = existingCoinResult.data
                val sellAmountInUnit = amountInFiat / price
                val balance = portfolioRepository.cashBalanceFlow().first()
                if (existingCoin == null || existingCoin.ownedAmountInUnit < sellAmountInUnit) {
                    return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
                }
                val remainingAmountInFiat = existingCoin.ownedAmountInFiat - amountInFiat
                val remainingAmountInUnit = existingCoin.ownedAmountInUnit - sellAmountInUnit
                if (remainingAmountInFiat < sellAllThreshold) {
                    portfolioRepository.removeCoinFromPortfolio(coin.id)
                } else {
                    portfolioRepository.savePortfolioCoin(
                        portfolioCoin = existingCoin.copy(
                            ownedAmountInUnit = remainingAmountInUnit,
                            ownedAmountInFiat = remainingAmountInFiat,
                        )
                    )
                }
                portfolioRepository.updateCashBalance(balance + amountInFiat)
                return Result.Success(Unit)
            }

            is Result.Error -> return existingCoinResult
        }
    }
}
