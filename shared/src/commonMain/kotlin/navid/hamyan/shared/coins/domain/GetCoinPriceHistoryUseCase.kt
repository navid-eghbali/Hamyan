package navid.hamyan.shared.coins.domain

import navid.hamyan.shared.coins.data.mapper.toPriceModel
import navid.hamyan.shared.coins.domain.api.CoinsRemoteDataSource
import navid.hamyan.shared.coins.domain.model.PriceModel
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.domain.map

class GetCoinPriceHistoryUseCase(
    private val client: CoinsRemoteDataSource,
) {
    suspend operator fun invoke(coinId: String): Result<List<PriceModel>, DataError.Remote> = client.getPriceHistory(coinId)
        .map { response ->
            response.data.history.map { it.toPriceModel() }
        }
}
