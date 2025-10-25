package navid.hamyan.shared

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import navid.hamyan.shared.coins.presentation.CoinsListScreen
import navid.hamyan.shared.core.navigation.Buy
import navid.hamyan.shared.core.navigation.Coins
import navid.hamyan.shared.core.navigation.Portfolio
import navid.hamyan.shared.core.navigation.Sell
import navid.hamyan.shared.portfolio.presentation.PortfolioScreen
import navid.hamyan.shared.theme.HamyanTheme
import navid.hamyan.shared.trade.presentation.buy.BuyScreen
import navid.hamyan.shared.trade.presentation.sell.SellScreen

@Composable
fun App() {
    val navController = rememberNavController()
    HamyanTheme {
        NavHost(
            navController = navController,
            startDestination = Portfolio,
            modifier = Modifier.fillMaxSize(),
        ) {
            composable<Portfolio> {
                PortfolioScreen(
                    onCoinItemClicked = { coinId -> navController.navigate(Sell(coinId)) },
                    onDiscoverCoinsClicked = { navController.navigate(Coins) },
                )
            }
            composable<Coins> {
                CoinsListScreen(
                    onCoinClicked = { coinId -> navController.navigate(Buy(coinId)) },
                )
            }
            composable<Buy> { navBackStackEntry ->
                val coinId = navBackStackEntry.toRoute<Buy>().coinId
                BuyScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Portfolio) {
                            popUpTo(Portfolio) { inclusive = true }
                        }
                    },
                )
            }
            composable<Sell> { navBackStackEntry ->
                val coinId = navBackStackEntry.toRoute<Sell>().coinId
                SellScreen(
                    coinId = coinId,
                    navigateToPortfolio = {
                        navController.navigate(Portfolio) {
                            popUpTo(Portfolio) { inclusive = true }
                        }
                    },
                )
            }
        }
    }
}
