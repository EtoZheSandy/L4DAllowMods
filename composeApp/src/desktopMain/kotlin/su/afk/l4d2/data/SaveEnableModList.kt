package su.afk.l4d2.data


import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import su.afk.l4d2.utils.AddonInfo
import java.util.prefs.Preferences


fun saveEnableAddonsList(modList: List<AddonInfo>) {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    val data = Json.encodeToString(modList)
    prefs.put("modList", data)
}

fun loadEnableAddonsList(): List<AddonInfo>? {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    val data = prefs.get("modList", null) ?: return null
    return Json.decodeFromString<List<AddonInfo>>(data)
}
