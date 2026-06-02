package su.afk.l4d2

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.icon


fun main() =
    application {
        val windowState = rememberWindowState()

        Window(
            onCloseRequest = ::exitApplication,
            title = "L4DModTool",
            state = windowState,
            resizable = true,
            icon = org.jetbrains.compose.resources.painterResource(Res.drawable.icon)
        ) {
            App()
        }
    }
