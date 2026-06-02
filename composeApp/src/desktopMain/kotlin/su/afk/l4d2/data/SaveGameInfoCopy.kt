package su.afk.l4d2.data

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

fun loadGameInfoContent(): String? {
    val file = StoragePaths.gameInfoBackupFile
    if (!file.exists()) return null

    val content = file.readText()
    return content.ifBlank { null }
}
