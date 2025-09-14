import androidx.compose.ui.window.ComposeUIViewController
import navid.hamyan.shared.App
import navid.hamyan.shared.di.initKoin
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = { initKoin() },
    content = { App() },
)
