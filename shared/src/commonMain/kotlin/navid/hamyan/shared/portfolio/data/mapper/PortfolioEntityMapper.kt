package navid.hamyan.shared.portfolio.data.mapper

import navid.hamyan.shared.core.domain.coin.Coin
import navid.hamyan.shared.portfolio.data.local.PortfolioCoinEntity
import navid.hamyan.shared.portfolio.domain.PortfolioCoinModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

fun PortfolioCoinEntity.toPortfolioCoinModel(
    currentPrice: Double,
): PortfolioCoinModel = PortfolioCoinModel(
    coin = Coin(
        id = coinId,
        name = name,
        symbol = symbol,
        iconUrl = iconUrl,
    ),
    performancePercent = ((currentPrice - averagePurchasePrice) / averagePurchasePrice) * 100,
    averagePurchasePrice = averagePurchasePrice,
    ownedAmountInUnit = amountOwned,
    ownedAmountInFiat = amountOwned * currentPrice,
)

@OptIn(ExperimentalTime::class)
fun PortfolioCoinModel.toPortfolioCoinEntity(): PortfolioCoinEntity = PortfolioCoinEntity(
    coinId = coin.id,
    name = coin.name,
    symbol = coin.symbol,
    iconUrl = coin.iconUrl,
    averagePurchasePrice = averagePurchasePrice,
    amountOwned = ownedAmountInUnit,
    timestamp = Clock.System.now().toEpochMilliseconds(),
)
