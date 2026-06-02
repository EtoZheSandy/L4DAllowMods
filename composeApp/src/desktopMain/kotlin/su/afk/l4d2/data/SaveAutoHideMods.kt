package su.afk.l4d2.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.prefs.Preferences


fun saveAutoHideMods(settings: Pair<Boolean, Int>) {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    val data = Json.encodeToString(settings)
    prefs.put("AutoHideMods", data)
}

fun loadAutoHideMods(): Pair<Boolean, Int>? {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    val data = prefs.get("AutoHideMods", null) ?: return null
    return try {
        Json.decodeFromString<Pair<Boolean, Int>>(data)
    } catch (_: Exception) {
        null
    }
}
