package su.afk.l4d2

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.icon
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.SwingUtilities


fun main() =
    application {
        val windowState = rememberWindowState()

        Window(
            onCloseRequest = ::exitApplication,
            title = "L4DModTool",
            undecorated = true,
            state = windowState,
            resizable = true,
            icon = org.jetbrains.compose.resources.painterResource(Res.drawable.icon)
        ) {
            val window = this.window // Получаем текущее окно

            var dragOffset by remember { mutableStateOf(Point(0, 0)) }

            DisposableEffect(Unit) {
                window.isLocationByPlatform = true // Позволяет платформе управлять первоначальным расположением окна

                val listener = object : MouseAdapter() {
                    override fun mousePressed(e: MouseEvent) {
                        // Проверяем, нажата ли левая кнопка мыши и не в правой области (под скроллбар)
                        if (SwingUtilities.isLeftMouseButton(e) && !isRightRestrictedArea(e)) {
                            dragOffset = e.point
                        }
                    }

                    override fun mouseDragged(e: MouseEvent) {
                        // Проверяем, нажата ли левая кнопка мыши и не в правой области (под скроллбар)
                        if (SwingUtilities.isLeftMouseButton(e) && !isRightRestrictedArea(e)) {
                            SwingUtilities.invokeLater {
                                val location = window.location
                                window.setLocation(
                                    location.x + e.x - dragOffset.x,
                                    location.y + e.y - dragOffset.y
                                )
                            }
                        }
                    }

                    private fun isRightRestrictedArea(e: MouseEvent): Boolean {
                        // Ширина области, которая не должна быть активной для перетаскивания (например, ширина скроллбара)
                        val restrictedWidth = 60 // Ширина области, где нельзя перетаскивать окно
                        return e.x >= window.width - restrictedWidth // Проверка нажатия в правой области
                    }

                }

                window.addMouseListener(listener)
                window.addMouseMotionListener(listener)

                onDispose {
                    window.removeMouseListener(listener)
                    window.removeMouseMotionListener(listener)
                }
            }

            App(onCloseRequest = ::exitApplication)
        }
    }
