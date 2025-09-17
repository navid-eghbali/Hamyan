package navid.hamyan.shared.coins.presentation

import androidx.compose.runtime.Stable
import org.jetbrains.compose.resources.StringResource

@Stable
data class CoinsState(
    val error: StringResource? = null,
    val coins: List<UiCoinListItem> = emptyList(),
    val chartState: ChartState? = null,
)

@Stable
data class ChartState(
    val sparkLine: List<Double> = emptyList(),
    val isLoading: Boolean = false,
    val coinName: String = "",
)
