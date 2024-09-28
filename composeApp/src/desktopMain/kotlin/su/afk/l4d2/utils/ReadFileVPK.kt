package su.afk.l4d2.utils

import kotlinx.serialization.Serializable
import su.afk.l4d2.data.loadCacheFromFile
import su.afk.l4d2.data.saveCacheToFile
import java.io.File

// Глобальный кэш для AddonInfo
val addonCache = mutableMapOf<String, AddonInfo>()

/** Проходит по всем .vpk файлам в указанной папке,
 * вызывает extractAddonInfo и parseAddonTitle для каждого, собирает список названий аддонов.*/
fun loadAddonInfo(workshopFolderPath: String): List<AddonInfo> {
    // Попробуем загрузить кэш из файла перед обработкой
    val loadedCache = loadCacheFromFile() // Загружаем кэш из файла
    addonCache.clear() // Очищаем текущий кэш
    addonCache.putAll(loadedCache) // Обновляем кэш новыми значениями

    val workshopFolder = File(workshopFolderPath)
    val addonInfoList = mutableListOf<AddonInfo>()

    // Получаем список .vpk файлов
    val vpkFiles =
        workshopFolder.listFiles { file -> file.extension.equals("vpk", ignoreCase = true) }
            ?: return emptyList()

    for (vpkFile in vpkFiles) {
        val filename = vpkFile.name

        // Проверка, есть ли данные в кэше
        val cachedAddon = addonCache[filename]
        if (cachedAddon != null) {
            addonInfoList.add(cachedAddon)
            continue // Пропускаем дальнейшую обработку, если данные уже закэшированы
        }

        val addonInfoContent = parseVpkFile(vpkFile)

        val (title, description) = if (addonInfoContent != null) {
            parseAddonInfo(addonInfoContent)
        } else {
            // Если addoninfo.txt отсутствует, используем имя файла .vpk
            Pair(vpkFile.nameWithoutExtension, null)
        }

        // Проверяем наличие изображения с тем же именем
        val imageFile = File(workshopFolder, "${vpkFile.nameWithoutExtension}.jpg")
        val imagePath = if (imageFile.exists()) imageFile.absolutePath else null

        val addonInfo = AddonInfo(
            title = title,
            description = description,
            filename = filename,
            imagePath = imagePath
        )
        // Добавляем его в список аддонов
        addonInfoList.add(addonInfo)

        // Обновляем кэш новыми данными
        addonCache[filename] = addonInfo
    }
    // Сохраняем обновленный кэш в файл
    saveCacheToFile(addonCache)

    return addonInfoList
}

/**
 * title: Название аддона (из addoninfo.txt или имя файла .vpk, если addoninfo.txt отсутствует).
 * description: Описание аддона (из addoninfo.txt, может быть null).
 * filename: Имя файла .vpk.
 * imagePath: Путь к изображению (если существует), иначе null.
 * */
@Serializable
data class AddonInfo(
    val title: String,
    val description: String?,
    val filename: String,
    val imagePath: String?
)

// parseAddonTitle: Использует регулярное выражение для извлечения addontitle из содержимого addoninfo.txt.
fun parseAddonInfo(addonInfoContent: String): Pair<String, String?> {
    val titleRegex = """addontitle\s+"([^"]+)"""".toRegex(RegexOption.IGNORE_CASE)
    val descriptionRegex = """addonDescription\s+"([^"]+)"""".toRegex(RegexOption.IGNORE_CASE)

    val titleMatch = titleRegex.find(addonInfoContent)
    val descriptionMatch = descriptionRegex.find(addonInfoContent)

    val title = titleMatch?.groups?.get(1)?.value ?: "Нет названия"
    val description = descriptionMatch?.groups?.get(1)?.value

    return Pair(title, description)
}


