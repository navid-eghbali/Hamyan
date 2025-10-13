package navid.hamyan.shared.trade.presentation.sell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import navid.hamyan.shared.trade.presentation.common.TradeScreen
import navid.hamyan.shared.trade.presentation.common.TradeType
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SellScreen(
    coinId: String,
    navigateToPortfolio: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<SellViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    TradeScreen(
        state = state,
        tradeType = TradeType.SELL,
        onAmountChange = viewModel::onAmountChanged,
        onSubmitClicked = viewModel::onSellClicked,
        modifier = modifier,
    )
}
