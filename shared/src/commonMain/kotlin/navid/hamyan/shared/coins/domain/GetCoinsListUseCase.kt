package navid.hamyan.shared.coins.domain

import navid.hamyan.shared.coins.data.mapper.toCoinModel
import navid.hamyan.shared.coins.domain.api.CoinsRemoteDataSource
import navid.hamyan.shared.coins.domain.model.CoinModel
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.domain.map

class GetCoinsListUseCase(
    private val client: CoinsRemoteDataSource,
) {
    suspend operator fun invoke(): Result<List<CoinModel>, DataError.Remote> = client.getListOfCoins()
        .map { response ->
            response.data.coins.map { it.toCoinModel() }
        }
}
