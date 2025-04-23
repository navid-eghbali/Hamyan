import androidx.compose.ui.window.ComposeUIViewController
import navid.hamyan.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
