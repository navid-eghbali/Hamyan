import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import navid.hamyan.shared.App
import navid.hamyan.shared.di.initKoin
import navid.hamyan.shared.resources.Res
import navid.hamyan.shared.resources.app_name
import org.jetbrains.compose.resources.stringResource
import java.awt.Dimension
import java.awt.Toolkit

fun main() = application {
    initKoin()
    Window(
        onCloseRequest = ::exitApplication,
        state = rememberWindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getWindowSize(),
        ),
        title = stringResource(Res.string.app_name),
    ) {
        window.minimumSize = Dimension(800, 600)
        App()
    }
}

private fun getWindowSize(): DpSize {
    val screenSize = Toolkit.getDefaultToolkit().screenSize
    val width = (screenSize.width * 0.8).toInt()
    val height = (screenSize.height * 0.8).toInt()
    return DpSize(
        width = width.dp,
        height = height.dp,
    )
}

@Preview
@Composable
fun AppPreview() {
    App()
}
