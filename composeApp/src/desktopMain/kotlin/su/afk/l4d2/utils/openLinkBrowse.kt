package su.afk.l4d2.utils

import java.awt.Desktop
import java.net.URI

/** Открывает ссылку в браузере по умолчанию. */
fun openGitHubLink(url: String) {
    if (Desktop.isDesktopSupported()) {
        Desktop.getDesktop().browse(URI(url))
    }
}