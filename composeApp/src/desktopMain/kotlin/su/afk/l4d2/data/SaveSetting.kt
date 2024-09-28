package su.afk.l4d2.data

import java.util.prefs.Preferences


fun saveFolderPath(path: String) {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    prefs.put("folderPath", path)
}

fun loadFolderPath(): String? {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    return prefs.get("folderPath", null)
}
