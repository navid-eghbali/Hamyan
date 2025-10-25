package navid.hamyan.shared.portfolio.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import navid.hamyan.shared.theme.HamyanTheme
import navid.hamyan.shared.theme.LocalHamyanColorsPalette
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PortfolioScreen(
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
) {
    val portfolioViewModel = koinViewModel<PortfolioViewModel>()
    val state by portfolioViewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize(),
        ) {
            CircularProgressIndicator(
                color = LocalHamyanColorsPalette.current.profitGreen,
                modifier = Modifier.size(32.dp),
            )
        }
    } else {
        PortfolioUi(
            state = state,
            onCoinItemClicked = onCoinItemClicked,
            onDiscoverCoinsClicked = onDiscoverCoinsClicked,
        )
    }
}

@Composable
fun PortfolioUi(
    state: PortfolioState,
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.inversePrimary),
    ) {
        PortfolioBalanceSection(
            portfolioValue = state.portfolioValue,
            cashBalance = state.cashBalance,
            showBuyButton = state.showBuyButton,
            onBuyButtonClicked = onDiscoverCoinsClicked,
        )
        PortfolioCoinsList(
            coins = state.coins,
            onCoinItemClicked = onCoinItemClicked,
            onDiscoverCoinsClicked = onDiscoverCoinsClicked,
        )
    }
}

@Composable
private fun PortfolioBalanceSection(
    portfolioValue: String,
    cashBalance: String,
    showBuyButton: Boolean,
    onBuyButtonClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxHeight(0.3f)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.inversePrimary)
            .padding(32.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Total Value:",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = portfolioValue,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineLarge,
            )
            Row {
                Text(
                    text = "Cash Balance: ",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = cashBalance,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            if (showBuyButton) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onBuyButtonClicked,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocalHamyanColorsPalette.current.profitGreen,
                    ),
                    contentPadding = PaddingValues(horizontal = 64.dp),
                ) {
                    Text(
                        text = "Buy Coin",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun PortfolioCoinsList(
    coins: List<UiPortfolioCoinItem>,
    onCoinItemClicked: (String) -> Unit,
    onDiscoverCoinsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
            .background(MaterialTheme.colorScheme.background),
    ) {
        if (coins.isEmpty()) {
            PortfolioEmptySection(onDiscoverCoinsClicked = onDiscoverCoinsClicked)
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                Text(
                    text = "💰Owned Coins:",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(16.dp),
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(coins) { coin ->
                        CoinListItem(
                            coin = coin,
                            onCoinItemClicked = onCoinItemClicked,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PortfolioEmptySection(
    onDiscoverCoinsClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
    ) {
        Text(
            text = "No coins in your portfolio yet.",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleSmall,
        )
        Button(
            onClick = onDiscoverCoinsClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = LocalHamyanColorsPalette.current.profitGreen,
            ),
            contentPadding = PaddingValues(horizontal = 64.dp)
        ) {
            Text(
                text = "Discover Coins",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun CoinListItem(
    coin: UiPortfolioCoinItem,
    onCoinItemClicked: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCoinItemClicked(coin.id) }
            .padding(16.dp),
    ) {
        AsyncImage(
            model = coin.iconUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(4.dp)
                .clip(CircleShape)
                .size(40.dp),
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
                text = coin.amountInUnitText,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
            )
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = coin.amountInFiatText,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = coin.performancePercentText,
                color = if (coin.isPositive) LocalHamyanColorsPalette.current.profitGreen else LocalHamyanColorsPalette.current.lossRed,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    }
}

@Preview
@Composable
private fun PortfolioUiPreview() {
    HamyanTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PortfolioUi(
                state = PortfolioState(),
                onCoinItemClicked = {},
                onDiscoverCoinsClicked = {},
            )
        }
    }
}
