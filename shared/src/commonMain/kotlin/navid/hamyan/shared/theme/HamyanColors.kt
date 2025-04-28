package navid.hamyan.shared.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class HamyanColorsPalette(
    val profitGreen: Color = Color.Unspecified,
    val lossRed: Color = Color.Unspecified,
)

internal val ProfitGreenLight = Color(0xFF32DE84)
internal val LossRedLight = Color(0xFFD2122E)
internal val ProfitGreenDark = Color(0xFF32DE84)
internal val LossRedDark = Color(0xFFD2122E)

internal val LightHamyanColorsScheme = HamyanColorsPalette(
    profitGreen = ProfitGreenLight,
    lossRed = LossRedLight,
)
internal val DarkHamyanColorsScheme = HamyanColorsPalette(
    profitGreen = ProfitGreenDark,
    lossRed = LossRedDark,
)

internal val LocalHamyanColorsPalette = compositionLocalOf { HamyanColorsPalette() }
