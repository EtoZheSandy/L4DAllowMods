package su.afk.l4d2.data

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.loadCacheFromFile
import kotlinproject.composeapp.generated.resources.saveCacheToFile
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import su.afk.l4d2.domain.model.AddonInfo

/**
 * Сохранение кэша в файл
 */
fun saveCacheToFile(addonCache: Map<String, AddonInfo>) {
    try {
        StoragePaths.appDataDir.mkdirs()
        val json = Json.encodeToString(addonCache.values.toList())
        StoragePaths.addonCacheFile.writeText(json)
    } catch (e: Exception) {
        LogSystem.addLog(1, Res.string.saveCacheToFile, e.message.orEmpty())
    }
}

/**
 * Загрузка кэша из файла
 */
fun loadCacheFromFile(): Map<String, AddonInfo> {
    val cacheFile = StoragePaths.addonCacheFile
    if (cacheFile.exists()) {
        try {
            val json = cacheFile.readText()
            val cachedAddons = Json.decodeFromString<List<AddonInfo>>(json)
            return cachedAddons.associateBy { it.filename }
        } catch (e: Exception) {
            LogSystem.addLog(1, Res.string.loadCacheFromFile, e.message.orEmpty())
        }
    }
    return emptyMap()
}

fun clearAddonMetadataCache() {
    val cacheFile = StoragePaths.addonCacheFile
    if (cacheFile.exists()) {
        cacheFile.delete()
    }
}
