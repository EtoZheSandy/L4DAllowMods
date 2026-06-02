package su.afk.l4d2.data

// Функция для сохранения содержимого файла gameinfo.txt в преференции
fun saveGameInfoContent(gameInfo: String?) {
    val file = StoragePaths.gameInfoBackupFile
    if (gameInfo.isNullOrBlank()) {
        if (file.exists()) {
            file.delete()
        }
        return
    }

    file.writeText(gameInfo)
}

// Функция для загрузки сохраненного содержимого gameinfo.txt
fun loadGameInfoContent(): String? {
    val file = StoragePaths.gameInfoBackupFile
    if (!file.exists()) return null

    val content = file.readText()
    return content.ifBlank { null }
}
