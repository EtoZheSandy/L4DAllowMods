package su.afk.l4d2.domain.service

import su.afk.l4d2.domain.model.AddonInfo
import su.afk.l4d2.utils.findGameInfo as findGameInfoFile
import su.afk.l4d2.utils.replaceGameInfoFile
import su.afk.l4d2.utils.updateGameInfoFile

class GameInfoService {
    fun findGameInfo(workshopFolderPath: String): Pair<String, String>? {
        return findGameInfoFile(workshopFolderPath)
    }

    fun updateGameInfo(addons: List<AddonInfo>, gameInfoFilePath: String) {
        updateGameInfoFile(addons = addons, gameInfoFilePath = gameInfoFilePath)
    }

    fun restoreGameInfo(gameInfoFilePath: String) {
        replaceGameInfoFile(gameInfoFilePath)
    }
}
