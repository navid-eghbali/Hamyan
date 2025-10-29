package navid.hamyan.shared.trade.presentation.buy

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.runComposeUiTest
import navid.hamyan.shared.resources.Res
import navid.hamyan.shared.resources.error_unknown
import navid.hamyan.shared.trade.presentation.common.TradeScreen
import navid.hamyan.shared.trade.presentation.common.TradeState
import navid.hamyan.shared.trade.presentation.common.TradeType
import navid.hamyan.shared.trade.presentation.common.UiTradeCoinItem
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class BuyScreenTest {
    @Test
    fun checkSubmitButtonLabelChangesWithTradeType() = runComposeUiTest {
        val state = TradeState(
            coin = UiTradeCoinItem(
                id = "bitcoin",
                name = "Bitcoin",
                symbol = "BTC",
                iconUrl = "url",
                price = 50000.0,
            ),
        )
        setContent {
            TradeScreen(
                state = state,
                tradeType = TradeType.BUY,
                onAmountChange = {},
                onSubmitClicked = {},
            )
        }
        onNodeWithText("Sell Now").assertDoesNotExist()
        onNodeWithText("Buy Now").assertExists()
        onNodeWithText("Buy Now").assertIsDisplayed()

        setContent {
            TradeScreen(
                state = state,
                tradeType = TradeType.SELL,
                onAmountChange = {},
                onSubmitClicked = {},
            )
        }
        onNodeWithText("Buy Now").assertDoesNotExist()
        onNodeWithText("Sell Now").assertExists()
        onNodeWithText("Sell Now").assertIsDisplayed()
    }

    @Test
    fun checkIfCoinNameShowProperlyInBuy() = runComposeUiTest {
        val state = TradeState(
            coin = UiTradeCoinItem(
                id = "ethereum",
                name = "Ethereum",
                symbol = "ETH",
                iconUrl = "url",
                price = 4000.0,
            )
        )
        setContent {
            TradeScreen(
                state = state,
                tradeType = TradeType.BUY,
                onAmountChange = {},
                onSubmitClicked = {},
            )
        }
        onNodeWithTag("trade_screen_coin_name").assertExists()
        onNodeWithTag("trade_screen_coin_name").assertTextEquals("Ethereum")
    }

    @Test
    fun checkErrorShownProperly() = runComposeUiTest {
        val state = TradeState(
            coin = UiTradeCoinItem(
                id = "dogecoin",
                name = "Dogecoin",
                symbol = "DOGE",
                iconUrl = "url",
                price = 0.25,
            ),
            error = Res.string.error_unknown,
        )
        setContent {
            TradeScreen(
                state = state,
                tradeType = TradeType.BUY,
                onAmountChange = {},
                onSubmitClicked = {},
            )
        }
        onNodeWithTag("trade_screen_error_text").assertExists()
        onNodeWithTag("trade_screen_error_text").assertIsDisplayed()
    }
}
