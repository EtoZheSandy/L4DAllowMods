package su.afk.l4d2.data

import java.io.File

// Функция для сохранения содержимого файла gameinfo.txt в преференции
fun saveGameInfoContent(gameInfo: String?) {
    val file = File(getAppDataDir(), "gameinfo.txt")
    file.writeText(gameInfo.orEmpty())
}

// Функция для загрузки сохраненного содержимого gameinfo.txt
fun loadGameInfoContent(): String? {
    val file = File(getAppDataDir(), "gameinfo.txt")
    return if (file.exists()) file.readText() else null
}