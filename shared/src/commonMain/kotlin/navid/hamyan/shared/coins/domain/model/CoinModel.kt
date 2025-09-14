package navid.hamyan.shared.coins.domain.model

import navid.hamyan.shared.core.domain.coin.Coin

data class CoinModel(
    val coin: Coin,
    val price: Double,
    val change: Double,
)
