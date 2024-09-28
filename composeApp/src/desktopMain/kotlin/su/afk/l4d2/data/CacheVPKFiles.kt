package su.afk.l4d2.data

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import su.afk.l4d2.utils.AddonInfo
import java.io.File

// Получаем домашнюю директорию пользователя
private val userHome = System.getProperty("user.home")

// Создаем поддиректорию для приложения, например, ".l4d4Tools" (обычно скрытые директории используются для данных конфигурации)
private val appDirectory = File(userHome, ".l4d4Tools")

// Указываем путь к файлу кэша
private val cacheFile = File(appDirectory, "addon_cache.json")

/**
 * Сохранение кэша в файл
 */
fun saveCacheToFile(addonCache: Map<String, AddonInfo>) {
    try {
        // Проверяем, существует ли директория, и создаем ее, если нет
        if (!appDirectory.exists()) {
            appDirectory.mkdirs()
        }
        val json =
            Json.encodeToString(addonCache.values.toList()) // Преобразуем список AddonInfo в JSON
        cacheFile.writeText(json) // Сохраняем JSON в файл
    } catch (e: Exception) {
        LogSystem.addLog(1, "Ошибка при сохранении кэша Addons: ${e.message}")
    }
}

/**
 * Загрузка кэша из файла
 */
fun loadCacheFromFile(): Map<String, AddonInfo> {
    if (cacheFile.exists()) {
        try {
            val json = cacheFile.readText() // Читаем JSON из файла
            val cachedAddons =
                Json.decodeFromString<List<AddonInfo>>(json) // Десериализуем в список AddonInfo
            return cachedAddons.associateBy { it.filename } // Преобразуем в Map с ключом filename
        } catch (e: Exception) {
            LogSystem.addLog(1, "Ошибка при загрузке кэша Addons: ${e.message}")
        }
    }
    return emptyMap() // Возвращаем пустую карту, если файл не найден или произошла ошибка
}