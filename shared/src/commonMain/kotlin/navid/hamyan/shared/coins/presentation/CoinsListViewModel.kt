package navid.hamyan.shared.coins.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import navid.hamyan.shared.coins.domain.GetCoinsListUseCase
import navid.hamyan.shared.core.domain.Result

class CoinsListViewModel(
    private val getCoinsListUseCase: GetCoinsListUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(CoinsState())
    val state = _state
        .onStart { getAllCoins() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CoinsState(),
        )

    private suspend fun getAllCoins() {
        when (val result = getCoinsListUseCase()) {
            is Result.Success -> _state.update {
                it.copy(
                    coins = result.data.map { coinItem ->
                        UiCoinListItem(
                            id = coinItem.coin.id,
                            name = coinItem.coin.name,
                            symbol = coinItem.coin.symbol,
                            iconUrl = coinItem.coin.iconUrl,
                            formattedPrice = coinItem.price.toString(),
                            formattedChange = coinItem.change.toString(),
                            isPositive = coinItem.change >= 0,
                        )
                    }
                )
            }

            is Result.Error -> _state.update {
                it.copy(
                    error = null,
                )
            }
        }
    }
}
