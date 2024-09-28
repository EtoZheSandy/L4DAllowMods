package su.afk.l4d2.data

import java.util.prefs.Preferences

// Функция для сохранения содержимого файла gameinfo.txt в преференции
fun saveGameInfoContent(gameInfo: String?) {
    val prefs = Preferences.userRoot().node("l4d4Tools") // Сохранение в узел преференций
    prefs.put("gameInfo", gameInfo) // Сохранение содержимого
}

// Функция для загрузки сохраненного содержимого gameinfo.txt
fun loadGameInfoContent(): String? {
    val prefs = Preferences.userRoot().node("l4d4Tools")
    return prefs.get("gameInfo", null) // Загрузка сохраненного содержимого
}