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
        val json =
            Json.encodeToString(addonCache.values.toList()) // Преобразуем список AddonInfo в JSON
        StoragePaths.addonCacheFile.writeText(json) // Сохраняем JSON в файл
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
            val json = cacheFile.readText() // Читаем JSON из файла
            val cachedAddons =
                Json.decodeFromString<List<AddonInfo>>(json) // Десериализуем в список AddonInfo
            return cachedAddons.associateBy { it.filename } // Преобразуем в Map с ключом filename
        } catch (e: Exception) {
            LogSystem.addLog(1, Res.string.loadCacheFromFile, e.message.orEmpty())
        }
    }
    return emptyMap() // Возвращаем пустую карту, если файл не найден или произошла ошибка
}

fun clearAddonMetadataCache() {
    val cacheFile = StoragePaths.addonCacheFile
    if (cacheFile.exists()) {
        cacheFile.delete()
    }
}
