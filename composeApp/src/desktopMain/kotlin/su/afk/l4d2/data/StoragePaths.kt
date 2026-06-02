package su.afk.l4d2.data

import java.io.File

object StoragePaths {
    val appDataDir: File
        get() = getAppDataDir()

    val addonCacheFile: File
        get() = File(appDataDir, "addon_cache.json")

    val enabledAddonsFile: File
        get() = File(appDataDir, "modList.json")

    val gameInfoBackupFile: File
        get() = File(appDataDir, "gameinfo.txt")
}
