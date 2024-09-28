package su.afk.l4d2.data

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.clearPreferencesDone
import kotlinproject.composeapp.generated.resources.clearPreferencesFail
import java.util.prefs.Preferences

// Функция для очистки всех преференций в узле "MyApp"
fun clearPreferences() {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    try {
        prefs.clear() // Очищает все данные в узле
        LogSystem.addLog(3, Res.string.clearPreferencesDone)
    } catch (e: Exception) {
        LogSystem.addLog(1, Res.string.clearPreferencesFail)
    }
}