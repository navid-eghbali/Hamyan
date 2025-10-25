package navid.hamyan.shared.trade.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import navid.hamyan.shared.theme.HamyanTheme
import navid.hamyan.shared.theme.LocalHamyanColorsPalette
import navid.hamyan.shared.trade.presentation.common.component.rememberCurrencyVisualTransformation
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun TradeScreen(
    state: TradeState,
    tradeType: TradeType,
    onAmountChange: (String) -> Unit,
    onSubmitClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = RoundedCornerShape(32.dp),
                    )
                    .padding(horizontal = 16.dp, vertical = 4.dp),
            ) {
                AsyncImage(
                    model = state.coin?.iconUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(4.dp).clip(CircleShape).size(24.dp),
                )
                Text(
                    text = state.coin?.name ?: "",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(4.dp),
                )
            }
            Text(
                text = when (tradeType) {
                    TradeType.BUY -> "Buy Amount"
                    TradeType.SELL -> "Sell Amount"
                },
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
            CenteredDollarTextField(
                amountText = state.amount,
                onAmountChange = onAmountChange,
            )
            Text(
                text = state.availableAmount,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(4.dp),
            )
            if (state.error != null) {
                Text(
                    text = stringResource(state.error),
                    color = LocalHamyanColorsPalette.current.lossRed,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(4.dp),
                )
            }
        }
        Button(
            onClick = onSubmitClicked,
            colors = ButtonDefaults.buttonColors(
                containerColor = when (tradeType) {
                    TradeType.BUY -> LocalHamyanColorsPalette.current.profitGreen
                    TradeType.SELL -> LocalHamyanColorsPalette.current.lossRed
                },
            ),
            contentPadding = PaddingValues(horizontal = 64.dp),
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 32.dp)
        ) {
            Text(
                text = when (tradeType) {
                    TradeType.BUY -> "Buy Now"
                    TradeType.SELL -> "Sell Now"
                },
                color = when (tradeType) {
                    TradeType.BUY -> MaterialTheme.colorScheme.onPrimary
                    TradeType.SELL -> MaterialTheme.colorScheme.onBackground
                },
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
private fun CenteredDollarTextField(
    amountText: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val displayText = amountText.trimStart('$')
    val currencyVisualTransformation = rememberCurrencyVisualTransformation()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    BasicTextField(
        value = displayText,
        onValueChange = { newValue ->
            val trimmed = newValue.trimStart('0').trim { it.isDigit().not() }
            if (trimmed.isEmpty() || trimmed.toInt() <= 10000) {
                onAmountChange(trimmed)
            }
        },
        textStyle = TextStyle(
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Number,
        ),
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.height(56.dp).wrapContentWidth(),
            ) {
                innerTextField()
            }
        },
        cursorBrush = SolidColor(Color.White),
        visualTransformation = currencyVisualTransformation,
        modifier = modifier
            .focusRequester(focusRequester)
            .padding(16.dp),
    )
}

@Preview
@Composable
private fun TradeScreenPreview() {
    HamyanTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TradeScreen(
                state = TradeState(
                    availableAmount = "Available: 0.005 BTC",
                    amount = "5000",
                ),
                tradeType = TradeType.BUY,
                onAmountChange = {},
                onSubmitClicked = {},
            )
        }
    }
}

enum class TradeType { BUY, SELL }
