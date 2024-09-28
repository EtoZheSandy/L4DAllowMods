package su.afk.l4d2.data

import java.util.prefs.Preferences

// Функция для очистки всех преференций в узле "MyApp"
fun clearPreferences() {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    try {
        prefs.clear() // Очищает все данные в узле
        LogSystem.addLog(3, "Все преференции очищены.")
    } catch (e: Exception) {
        LogSystem.addLog(1, "Ошибка при очистке преференций: ${e.message}")
    }
}