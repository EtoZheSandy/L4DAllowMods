package su.afk.l4d2.data


import kotlinx.serialization.json.Json
import su.afk.l4d2.utils.AddonInfo
import java.io.File

fun saveEnableAddonsList(modList: List<AddonInfo>) {
    val file = File(getAppDataDir(), "modList.json")
    val json = Json.encodeToString(modList)
    file.writeText(json)
}

fun loadEnableAddonsList(): List<AddonInfo>? {
    val file = File(getAppDataDir(), "modList.json")
    if (!file.exists()) return null
    return Json.decodeFromString(file.readText())
}
