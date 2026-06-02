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

// Функция для обрезки двух последних папок из пути
fun findGameInfo(originalPath: String): Pair<String, String>? {
    val workshopFolder = File(originalPath)
    val gameFolder = workshopFolder.parentFile?.parentFile

    if (gameFolder == null) {
        LogSystem.addLog(1, Res.string.findGameInfoPathFail, originalPath)
        return null
    }

    // Путь к файлу gameinfo.txt
    val gameInfoFilePath = File(gameFolder, "gameinfo.txt")

    // Проверяем существование файла и возвращаем путь и содержимое
    return if (gameInfoFilePath.exists() && gameInfoFilePath.isFile) {
        gameInfoFilePath.absolutePath to gameInfoFilePath.readText() // Возвращаем путь и содержимое как пару
    } else {
        LogSystem.addLog(1, Res.string.findGameInfoPathNotFound, originalPath)
        null
    }
}

// Функция для создания папки, перемещения файла и его переименования
fun processAddonFile(addons: List<AddonInfo>, basePath: String) {

    for (addon in addons) {
        // Определяем полный путь к файлу .vpk
        val originalFile = File(basePath, addon.filename)

        // Проверяем, существует ли файл .vpk
        if (!originalFile.exists() || !originalFile.isFile) {
            LogSystem.addLog(2, Res.string.processAddonFilePathFail, addon.filename, basePath)
            continue
        }

        // Создаем путь к новой папке на основе имени файла без расширения
        val newFolderPath = File(basePath, addon.filename.substringBeforeLast("."))

        // Определяем путь для перемещенного и переименованного файла
        val newFile = File(newFolderPath, "pak01_dir.vpk")

        // Копируем и переименовываем файл
        try {
            newFolderPath.mkdirs()
            originalFile.copyTo(
                newFile,
                overwrite = true
            ) // Копируем файл в новую папку с новым именем
            LogSystem.addLog(4, Res.string.processAddonFileDone, addon.filename, newFile.absolutePath)
        } catch (e: Exception) {
            LogSystem.addLog(2, Res.string.processAddonFileCopyFail, addon.filename, e.message)
        }
    }
}

// Функция для удаления созданных папок
fun deleteAddonFolders(addons: List<AddonInfo>, basePath: String) {
    for (addon in addons) {
        // Путь к папке, созданной для этого мода
        val folderPath = File(basePath, addon.filename.substringBeforeLast("."))

        // Проверяем, существует ли папка
        if (folderPath.exists() && folderPath.isDirectory) {
            try {
                // Удаляем все содержимое папки перед её удалением
                folderPath.deleteRecursively() // Удаляет папку и всё её содержимое
                LogSystem.addLog(4, Res.string.deleteAddonFoldersDone, folderPath.absolutePath)
            } catch (e: Exception) {
                LogSystem.addLog(2, Res.string.deleteAddonFoldersFail, folderPath.absolutePath, e.message)
            }
        } else {
//            LogSystem.addLog(1, "Папка ${folderPath.absolutePath} не найдена или это не папка.")
        }
    }

    LogSystem.addLog(3, Res.string.deleteAddonFoldersResult)
}

// Функция для добавления путей в блок SearchPaths в gameinfo.txt
fun updateGameInfoFile(addons: List<AddonInfo>, gameInfoFilePath: String) {
    // Читаем содержимое файла gameinfo.txt
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

    // Ищем место для блока SearchPaths и закрывающую скобку с учетом вложенности.
    val searchBlockStart = content.indexOf("{", searchPathsNameIndex)
    val searchBlockEnd = findMatchingBrace(content, searchBlockStart)
    if (searchBlockStart == -1 || searchBlockEnd == -1) {
        LogSystem.addLog(4, Res.string.updateGameInfoFileSearchBlockEnd)
        return
    }

    // Извлекаем существующий блок с путями
    val existingPathsBlock = content.substring(searchBlockStart + 1, searchBlockEnd)
    val cleanedPathsBlock = removeManagedAddonPaths(existingPathsBlock, addons)

    // Генерируем новые строки для добавления в блок, исключая дубли
    val newPaths = addons.map { addon ->
        """            Game                left4dead2\addons\workshop\${addon.filename.substringBeforeLast(".")}"""
    }.filterNot { newPath ->
        cleanedPathsBlock.contains(newPath) // Проверяем, есть ли путь уже в блоке
    }

    // Если нет новых путей для добавления, выходим из функции
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

    // Формируем обновленное содержимое файла, вставляя новые пути в начало блока с путями
    val updatedContent = buildString {
        append(content.substring(0, searchBlockStart + 1)) // Все до открывающей скобки блока
        append(managedBlock)                              // Добавляем управляемые приложением пути
        append(cleanedPathsBlock)                         // Существующие пути
        append(content.substring(searchBlockEnd))         // Все после закрывающей скобки блока
    }

    // Сохраняем обновленное содержимое обратно в файл
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





// Функция для замены содержимого файла gameinfo.txt на сохраненное в преференциях
fun replaceGameInfoFile(gameInfoFilePath: String) {
    // Загрузить сохраненное содержимое из преференций
    val savedContent = loadGameInfoContent()

    if (savedContent == null) {
        LogSystem.addLog(1, Res.string.replaceGameInfoFileSavedContentNull)
        return
    }

    val gameInfoFile = File(gameInfoFilePath)

    if (gameInfoFile.exists() && gameInfoFile.isFile) {
        // Записываем новое содержимое в файл
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
