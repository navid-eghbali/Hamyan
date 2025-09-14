package navid.hamyan.shared.coins.domain.api

import navid.hamyan.shared.coins.data.remote.dto.CoinDetailsResponseDto
import navid.hamyan.shared.coins.data.remote.dto.CoinPriceHistoryResponseDto
import navid.hamyan.shared.coins.data.remote.dto.CoinsResponseDto
import navid.hamyan.shared.core.domain.DataError
import navid.hamyan.shared.core.domain.Result

interface CoinsRemoteDataSource {
    suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote>
    suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote>
    suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote>
}
