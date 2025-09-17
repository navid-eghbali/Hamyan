package navid.hamyan.shared.coins.data.mapper

import navid.hamyan.shared.coins.data.remote.dto.CoinItemDto
import navid.hamyan.shared.coins.data.remote.dto.CoinPriceDto
import navid.hamyan.shared.coins.domain.model.CoinModel
import navid.hamyan.shared.coins.domain.model.PriceModel
import navid.hamyan.shared.core.domain.coin.Coin

fun CoinItemDto.toCoinModel(): CoinModel = CoinModel(
    coin = Coin(
        id = uuid,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
    ),
    price = price,
    change = change,
)

fun CoinPriceDto.toPriceModel() = PriceModel(
    price = price ?: 0.0,
    timestamp = timestamp,
)
