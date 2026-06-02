package su.afk.l4d2.data


import kotlinx.serialization.json.Json
import su.afk.l4d2.domain.model.AddonInfo

fun saveEnableAddonsList(modList: List<AddonInfo>) {
    try {
        val json = Json.encodeToString(modList)
        StoragePaths.enabledAddonsFile.writeText(json)
    } catch (_: Exception) {
    }
}

fun loadEnableAddonsList(): List<AddonInfo>? {
    val file = StoragePaths.enabledAddonsFile
    if (!file.exists()) return null
    return try {
        Json.decodeFromString(file.readText())
    } catch (_: Exception) {
        null
    }
}

fun clearEnableAddonsList() {
    val file = StoragePaths.enabledAddonsFile
    if (file.exists()) {
        file.delete()
    }
}
