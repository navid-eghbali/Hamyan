package navid.hamyan.shared.coins.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import navid.hamyan.shared.coins.presentation.component.PerformanceChart
import navid.hamyan.shared.theme.HamyanTheme
import navid.hamyan.shared.theme.LocalHamyanColorsPalette
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CoinsListScreen(
    onCoinClicked: (String) -> Unit,
) {
    val viewModel = koinViewModel<CoinsListViewModel>()
    val state by viewModel.state.collectAsStateWithLifecycle()

    CoinsListUi(
        state = state,
        onCoinClicked = onCoinClicked,
        onCoinLongPressed = viewModel::onCoinLongPressed,
        onChartDismissed = viewModel::onChartDismissed,
    )
}

@Composable
private fun CoinsListUi(
    state: CoinsState,
    onCoinClicked: (String) -> Unit,
    onCoinLongPressed: (String) -> Unit,
    onChartDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (state.chartState != null) {
            CoinChartDialog(
                state = state.chartState,
                onDismissed = onChartDismissed,
            )
        }
        CoinsList(
            coins = state.coins,
            onCoinClicked = onCoinClicked,
            onCoinLongPressed = onCoinLongPressed,
        )
    }
}

@Composable
private fun CoinsList(
    coins: List<UiCoinListItem>,
    onCoinClicked: (String) -> Unit,
    onCoinLongPressed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item {
                Text(
                    text = "🔥Top Coins:",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp),
                )
            }
            items(coins) { coin ->
                CoinListItem(
                    coin = coin,
                    onCoinClicked = onCoinClicked,
                    onCoinLongPressed = onCoinLongPressed,
                )
            }
        }
    }
}

@Composable
private fun CoinListItem(
    coin: UiCoinListItem,
    onCoinClicked: (String) -> Unit,
    onCoinLongPressed: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onCoinClicked(coin.id) },
                onLongClick = { onCoinLongPressed(coin.id) },
            )
            .padding(16.dp),
    ) {
        AsyncImage(
            model = coin.iconUrl,
            contentDescription = coin.name,
            contentScale = ContentScale.Fit,
            modifier = Modifier.padding(4.dp).clip(CircleShape).size(40.dp),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = coin.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = coin.symbol,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = coin.formattedPrice,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = coin.formattedChange,
                color = if (coin.isPositive) LocalHamyanColorsPalette.current.profitGreen else LocalHamyanColorsPalette.current.lossRed,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Composable
private fun CoinChartDialog(
    state: ChartState,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AlertDialog(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = onDismissed,
        title = { Text(text = "30d Price Chart for ${state.coinName}") },
        text = {
            if (state.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(32.dp))
                }
            } else {
                PerformanceChart(
                    nodes = state.sparkLine,
                    profitColor = LocalHamyanColorsPalette.current.profitGreen,
                    lossColor = LocalHamyanColorsPalette.current.lossRed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(16.dp),
                )
            }
        },
        confirmButton = {},
        dismissButton = {
            Button(onClick = onDismissed) {
                Text(text = "Close")
            }
        },
    )
}

@Preview
@Composable
private fun CoinsListUiPreview() {
    HamyanTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CoinsListUi(
                state = CoinsState(),
                onCoinClicked = {},
                onCoinLongPressed = {},
                onChartDismissed = {},
            )
        }
    }
}
