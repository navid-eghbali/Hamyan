package navid.hamyan.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import navid.hamyan.shared.coins.presentation.CoinsListScreen
import navid.hamyan.shared.theme.HamyanTheme

@Composable
fun App() {
    HamyanTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) {
            CoinsListScreen(
                onCoinClicked = {},
            )
        }
    }
}
