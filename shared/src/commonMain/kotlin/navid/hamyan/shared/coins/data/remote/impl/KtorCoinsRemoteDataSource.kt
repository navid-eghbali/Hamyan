package navid.hamyan.shared.coins.data.remote.impl

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import navid.hamyan.shared.coins.data.remote.dto.CoinDetailsResponseDto
import navid.hamyan.shared.coins.data.remote.dto.CoinPriceHistoryResponseDto
import navid.hamyan.shared.coins.data.remote.dto.CoinsResponseDto
import navid.hamyan.shared.coins.domain.api.CoinsRemoteDataSource
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.Result
import navid.hamyan.shared.core.network.safeCall

private const val BASE_URL = "https://api.coinranking.com/v2"

class KtorCoinsRemoteDataSource(
    private val httpClient: HttpClient,
) : CoinsRemoteDataSource {
    override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> = safeCall {
        httpClient.get("$BASE_URL/coins")
    }

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> = safeCall {
        httpClient.get("$BASE_URL/coin/$coinId/price-history")
    }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> = safeCall {
        httpClient.get("$BASE_URL/coin/$coinId")
    }
}
