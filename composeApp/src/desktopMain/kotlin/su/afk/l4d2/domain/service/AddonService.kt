package su.afk.l4d2.domain.service

import su.afk.l4d2.data.clearAddonMetadataCache
import su.afk.l4d2.domain.model.AddonInfo
import su.afk.l4d2.utils.deleteAddonFolders
import su.afk.l4d2.utils.loadAddonInfo
import su.afk.l4d2.utils.processAddonFile

class AddonService {
    fun loadAddons(workshopFolderPath: String): List<AddonInfo> {
        return loadAddonInfo(workshopFolderPath)
    }

    fun prepareAddons(addons: List<AddonInfo>, workshopFolderPath: String) {
        processAddonFile(addons = addons, basePath = workshopFolderPath)
    }

    fun clearPreparedAddons(addons: List<AddonInfo>, workshopFolderPath: String) {
        deleteAddonFolders(addons = addons, basePath = workshopFolderPath)
        clearAddonMetadataCache()
    }
}
