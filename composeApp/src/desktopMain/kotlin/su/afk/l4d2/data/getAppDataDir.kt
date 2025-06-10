package su.afk.l4d2.data

import java.io.File

fun getAppDataDir(): File {
    val os = System.getProperty("os.name").lowercase()
    val baseDir = when {
        os.contains("win") -> System.getenv("APPDATA") // AppData\Roaming
        os.contains("mac") -> System.getProperty("user.home") + "/Library/Application Support"
        else -> System.getProperty("user.home") + "/.config"
    }
    val appDir = File(baseDir, "L4D4Tools")
    if (!appDir.exists()) appDir.mkdirs()
    return appDir
}
