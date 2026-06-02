package su.afk.l4d2.utils

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.deleteAddonFoldersDone
import kotlinproject.composeapp.generated.resources.deleteAddonFoldersFail
import kotlinproject.composeapp.generated.resources.deleteAddonFoldersResult
import kotlinproject.composeapp.generated.resources.findGameInfoPathFail
import kotlinproject.composeapp.generated.resources.findGameInfoPathNotFound
import kotlinproject.composeapp.generated.resources.processAddonFileCopyFail
import kotlinproject.composeapp.generated.resources.processAddonFileDone
import kotlinproject.composeapp.generated.resources.processAddonFilePathFail
import kotlinproject.composeapp.generated.resources.replaceGameInfoFileDone
import kotlinproject.composeapp.generated.resources.replaceGameInfoFileFailPath
import kotlinproject.composeapp.generated.resources.replaceGameInfoFileSavedContentNull
import kotlinproject.composeapp.generated.resources.updateGameInfoDone
import kotlinproject.composeapp.generated.resources.updateGameInfoFailSave
import kotlinproject.composeapp.generated.resources.updateGameInfoFilePath
import kotlinproject.composeapp.generated.resources.updateGameInfoFileSearchBlockEnd
import kotlinproject.composeapp.generated.resources.updateGameInfoFileSearchBlockStart
import kotlinproject.composeapp.generated.resources.updateGameInfoNewPathsEmpty
import su.afk.l4d2.data.LogSystem
import su.afk.l4d2.data.loadGameInfoContent
import su.afk.l4d2.domain.model.AddonInfo
import java.io.File

private const val MANAGED_BLOCK_START = "            // L4DAllowMods managed paths start"
private const val MANAGED_BLOCK_END = "            // L4DAllowMods managed paths end"

fun findGameInfo(originalPath: String): Pair<String, String>? {
    val workshopFolder = File(originalPath)
    val gameFolder = workshopFolder.parentFile?.parentFile

    if (gameFolder == null) {
        LogSystem.addLog(1, Res.string.findGameInfoPathFail, originalPath)
        return null
    }

    val gameInfoFilePath = File(gameFolder, "gameinfo.txt")

    return if (gameInfoFilePath.exists() && gameInfoFilePath.isFile) {
        gameInfoFilePath.absolutePath to gameInfoFilePath.readText()
    } else {
        LogSystem.addLog(1, Res.string.findGameInfoPathNotFound, originalPath)
        null
    }
}

fun processAddonFile(addons: List<AddonInfo>, basePath: String) {

    for (addon in addons) {
        val originalFile = File(basePath, addon.filename)

        if (!originalFile.exists() || !originalFile.isFile) {
            LogSystem.addLog(2, Res.string.processAddonFilePathFail, addon.filename, basePath)
            continue
        }

        val newFolderPath = File(basePath, addon.filename.substringBeforeLast("."))
        val newFile = File(newFolderPath, "pak01_dir.vpk")

        try {
            newFolderPath.mkdirs()
            originalFile.copyTo(
                newFile,
                overwrite = true
            )
            LogSystem.addLog(4, Res.string.processAddonFileDone, addon.filename, newFile.absolutePath)
        } catch (e: Exception) {
            LogSystem.addLog(2, Res.string.processAddonFileCopyFail, addon.filename, e.message)
        }
    }
}

fun deleteAddonFolders(addons: List<AddonInfo>, basePath: String) {
    for (addon in addons) {
        val folderPath = File(basePath, addon.filename.substringBeforeLast("."))

        if (folderPath.exists() && folderPath.isDirectory) {
            try {
                folderPath.deleteRecursively()
                LogSystem.addLog(4, Res.string.deleteAddonFoldersDone, folderPath.absolutePath)
            } catch (e: Exception) {
                LogSystem.addLog(2, Res.string.deleteAddonFoldersFail, folderPath.absolutePath, e.message)
            }
        }
    }

    LogSystem.addLog(3, Res.string.deleteAddonFoldersResult)
}

fun updateGameInfoFile(addons: List<AddonInfo>, gameInfoFilePath: String) {
    val gameInfoFile = File(gameInfoFilePath)

    if (!gameInfoFile.exists() || !gameInfoFile.isFile) {
        LogSystem.addLog(1, Res.string.updateGameInfoFilePath, gameInfoFilePath)
        return
    }

    val content = gameInfoFile.readText()

    val searchPathsNameIndex = content.indexOf("SearchPaths", ignoreCase = true)
    if (searchPathsNameIndex == -1) {
        LogSystem.addLog(4, Res.string.updateGameInfoFileSearchBlockStart)
        return
    }

    // SearchPaths is a nested block, so the closing brace must be matched by depth.
    val searchBlockStart = content.indexOf("{", searchPathsNameIndex)
    val searchBlockEnd = findMatchingBrace(content, searchBlockStart)
    if (searchBlockStart == -1 || searchBlockEnd == -1) {
        LogSystem.addLog(4, Res.string.updateGameInfoFileSearchBlockEnd)
        return
    }

    val existingPathsBlock = content.substring(searchBlockStart + 1, searchBlockEnd)
    val cleanedPathsBlock = removeManagedAddonPaths(existingPathsBlock, addons)

    val newPaths = addons.map { addon ->
        """            Game                left4dead2\addons\workshop\${addon.filename.substringBeforeLast(".")}"""
    }.filterNot { newPath ->
        cleanedPathsBlock.contains(newPath)
    }

    if (newPaths.isEmpty() && cleanedPathsBlock == existingPathsBlock) {
        LogSystem.addLog(4, Res.string.updateGameInfoNewPathsEmpty)
        return
    }

    val managedBlock = if (newPaths.isEmpty()) {
        ""
    } else {
        buildString {
            append("\n")
            append(MANAGED_BLOCK_START)
            append("\n")
            append(newPaths.joinToString(separator = "\n"))
            append("\n")
            append(MANAGED_BLOCK_END)
            append("\n")
        }
    }

    val updatedContent = buildString {
        append(content.substring(0, searchBlockStart + 1))
        append(managedBlock)
        append(cleanedPathsBlock)
        append(content.substring(searchBlockEnd))
    }

    try {
        gameInfoFile.writeText(updatedContent)
        LogSystem.addLog(3, Res.string.updateGameInfoDone)
    } catch (e: Exception) {
        LogSystem.addLog(1, Res.string.updateGameInfoFailSave, e.message)
    }
}

private fun findMatchingBrace(content: String, openBraceIndex: Int): Int {
    if (openBraceIndex == -1) return -1

    var depth = 0
    for (index in openBraceIndex until content.length) {
        when (content[index]) {
            '{' -> depth++
            '}' -> {
                depth--
                if (depth == 0) return index
            }
        }
    }

    return -1
}

private fun removeManagedAddonPaths(pathsBlock: String, addons: List<AddonInfo>): String {
    val blockWithoutManagedSection = pathsBlock.replace(
        Regex(
            pattern = "\\R?\\s*// L4DAllowMods managed paths start[\\s\\S]*?// L4DAllowMods managed paths end\\R?",
            option = RegexOption.IGNORE_CASE
        ),
        "\n"
    )

    val managedPathValues = addons
        .map { addon -> """left4dead2\addons\workshop\${addon.filename.substringBeforeLast(".")}""" }
        .toSet()

    return blockWithoutManagedSection
        .lineSequence()
        .filterNot { line -> managedPathValues.any { managedPath -> line.contains(managedPath) } }
        .joinToString(separator = "\n")
}
fun replaceGameInfoFile(gameInfoFilePath: String) {
    val savedContent = loadGameInfoContent()

    if (savedContent == null) {
        LogSystem.addLog(1, Res.string.replaceGameInfoFileSavedContentNull)
        return
    }

    val gameInfoFile = File(gameInfoFilePath)

    if (gameInfoFile.exists() && gameInfoFile.isFile) {
        try {
            gameInfoFile.writeText(savedContent)
            LogSystem.addLog(3, Res.string.replaceGameInfoFileDone)
        } catch (e: Exception) {
            LogSystem.addLog(1, Res.string.updateGameInfoFailSave, e.message.orEmpty())
        }
    } else {
        LogSystem.addLog(1, Res.string.replaceGameInfoFileFailPath, gameInfoFilePath)
    }
}
