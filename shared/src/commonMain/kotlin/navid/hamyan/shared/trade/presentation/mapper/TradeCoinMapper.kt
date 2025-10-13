package navid.hamyan.shared.trade.presentation.mapper

import navid.hamyan.shared.core.domain.coin.Coin
import navid.hamyan.shared.trade.presentation.common.UiTradeCoinItem

fun UiTradeCoinItem.toCoin(): Coin = Coin(
    id = id,
    name = name,
    symbol = symbol,
    iconUrl = iconUrl,
)
