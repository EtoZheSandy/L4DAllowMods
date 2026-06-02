package su.afk.l4d2.utils

import su.afk.l4d2.data.loadCacheFromFile
import su.afk.l4d2.data.saveCacheToFile
import su.afk.l4d2.domain.model.AddonInfo
import java.io.File

val addonCache = mutableMapOf<String, AddonInfo>()

/** Читает метаданные аддонов из workshop-папки и переиспользует файловый кэш. */
fun loadAddonInfo(workshopFolderPath: String): List<AddonInfo> {
    val loadedCache = loadCacheFromFile()
    addonCache.clear()
    addonCache.putAll(loadedCache)

    val workshopFolder = File(workshopFolderPath)
    val addonInfoList = mutableListOf<AddonInfo>()

    val vpkFiles =
        workshopFolder.listFiles { file -> file.extension.equals("vpk", ignoreCase = true) }
            ?: return emptyList()

    for (vpkFile in vpkFiles) {
        val filename = vpkFile.name

        val cachedAddon = addonCache[filename]
        if (cachedAddon != null) {
            addonInfoList.add(cachedAddon)
            continue
        }

        val addonInfoContent = parseVpkFile(vpkFile)

        val (title, description) = if (addonInfoContent != null) {
            parseAddonInfo(addonInfoContent)
        } else {
            Pair(vpkFile.nameWithoutExtension, null)
        }

        val imageFile = File(workshopFolder, "${vpkFile.nameWithoutExtension}.jpg")
        val imagePath = if (imageFile.exists()) imageFile.absolutePath else null

        val addonInfo = AddonInfo(
            title = title,
            description = description,
            filename = filename,
            imagePath = imagePath
        )
        addonInfoList.add(addonInfo)
        addonCache[filename] = addonInfo
    }
    saveCacheToFile(addonCache)

    return addonInfoList
}

fun parseAddonInfo(addonInfoContent: String): Pair<String, String?> {
    val titleRegex = """addontitle\s+"([^"]+)"""".toRegex(RegexOption.IGNORE_CASE)
    val descriptionRegex = """addonDescription\s+"([^"]+)"""".toRegex(RegexOption.IGNORE_CASE)

    val titleMatch = titleRegex.find(addonInfoContent)
    val descriptionMatch = descriptionRegex.find(addonInfoContent)

    val title = titleMatch?.groups?.get(1)?.value ?: "Нет названия"
    val description = descriptionMatch?.groups?.get(1)?.value

    return Pair(title, description)
}
