package su.afk.l4d2.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import su.afk.l4d2.utils.AddonInfo
import java.util.prefs.Preferences


fun saveAutoHideMods(settings: Pair<Boolean, Int>) {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    val data = Json.encodeToString(settings)
    prefs.put("AutoHideMods", data)
}

fun loadAutoHideMods(): Pair<Boolean, Int>? {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    val data = prefs.get("AutoHideMods", null) ?: return null
    return Json.decodeFromString<Pair<Boolean, Int>>(data)
}