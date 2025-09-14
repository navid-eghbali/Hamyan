package navid.hamyan.shared.coins.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class CoinDetailsResponseDto(
    val data: CoinDetailsDto,
)

@Serializable
data class CoinDetailsDto(
    val coin: CoinItemDto,
)
