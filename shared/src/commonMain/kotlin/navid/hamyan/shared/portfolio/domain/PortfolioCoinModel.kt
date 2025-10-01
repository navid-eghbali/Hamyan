package navid.hamyan.shared.portfolio.domain

import navid.hamyan.shared.core.domain.coin.Coin

data class PortfolioCoinModel(
    val coin: Coin,
    val performancePercent: Double,
    val averagePurchasePrice: Double,
    val ownedAmountInUnit: Double,
    val ownedAmountInFiat: Double,
)
