package navid.hamyan.shared.coins.domain

import navid.hamyan.shared.coins.data.mapper.toCoinModel
import navid.hamyan.shared.coins.domain.api.CoinsRemoteDataSource
import navid.hamyan.shared.coins.domain.model.CoinModel
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.domain.map

class GetCoinDetailsUseCase(
    private val client: CoinsRemoteDataSource,
) {
    suspend operator fun invoke(coinId: String): Result<CoinModel, DataError.Remote> = client.getCoinById(coinId)
        .map { response ->
            response.data.coin.toCoinModel()
        }
}
