package su.afk.l4d2.utils

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.deleteAddonFoldersDone
import kotlinproject.composeapp.generated.resources.deleteAddonFoldersFail
import kotlinproject.composeapp.generated.resources.deleteAddonFoldersFailFolders
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
import java.io.File

// Функция для обрезки двух последних папок из пути
fun findGameInfo(originalPath: String): Pair<String, String>? {
    // Разделение пути на компоненты
    val pathComponents = originalPath.split(File.separator)
    if (pathComponents.size < 3) {
        LogSystem.addLog(1, Res.string.findGameInfoPathFail, originalPath)
        return null
    }

    // Создаем новый путь без двух последних папок
    val trimmedPath = pathComponents.dropLast(2).joinToString(File.separator)

    // Путь к файлу gameinfo.txt
    val gameInfoFilePath = File(trimmedPath, "gameinfo.txt")

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
        println("addon.filename: ${addon.filename}, basePath: $basePath")
        // Определяем полный путь к файлу .vpk
        val originalFile = File(basePath, addon.filename)

        // Проверяем, существует ли файл .vpk
        if (!originalFile.exists() || !originalFile.isFile) {
            LogSystem.addLog(2, Res.string.processAddonFilePathFail, addon.filename, basePath)
            return
        }

        // Создаем путь к новой папке на основе имени файла без расширения
        val newFolderPath = File(basePath, addon.filename.substringBeforeLast("."))
        if (!newFolderPath.exists()) {
            // Создаем новую папку
            newFolderPath.mkdir()
        }

        // Определяем путь для перемещенного и переименованного файла
        val newFile = File(newFolderPath, "pak01_dir.vpk")

        // Копируем и переименовываем файл
        try {
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

    // Дополнительно проверяем и удаляем все папки в basePath, если они не содержат поддиректорий
    val baseDirectory = File(basePath)
    baseDirectory.listFiles()?.forEach { folder ->
        if (folder.isDirectory) {
            val containsSubdirectories = folder.listFiles()?.any { it.isDirectory } == true

            if (!containsSubdirectories) {
                try {
                    folder.deleteRecursively()
                    LogSystem.addLog(4, Res.string.deleteAddonFoldersDone, folder.absolutePath)
                } catch (e: Exception) {
                    LogSystem.addLog(2, Res.string.deleteAddonFoldersFail, folder.absolutePath, e.message)
                }
            } else {
                LogSystem.addLog(4, Res.string.deleteAddonFoldersFailFolders, folder.absolutePath)
            }
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

    // Ищем место для блока, содержащего Game update и другие пути
    val searchBlockStart = content.indexOf("{", content.indexOf("SearchPaths"))
    if (searchBlockStart == -1) {
        LogSystem.addLog(4, Res.string.updateGameInfoFileSearchBlockStart)
        return
    }

    // Ищем закрывающую скобку для этого блока
    val searchBlockEnd = content.indexOf("}", searchBlockStart)
    if (searchBlockEnd == -1) {
        LogSystem.addLog(4, Res.string.updateGameInfoFileSearchBlockEnd)
        return
    }

    // Извлекаем существующий блок с путями
    val existingPathsBlock = content.substring(searchBlockStart, searchBlockEnd)

    // Генерируем новые строки для добавления в блок, исключая дубли
    val newPaths = addons.map { addon ->
        """            Game                left4dead2\addons\workshop\${addon.filename.substringBeforeLast(".")}"""
    }.filterNot { newPath ->
        existingPathsBlock.contains(newPath) // Проверяем, есть ли путь уже в блоке
    }.joinToString(separator = "\n")

    // Если нет новых путей для добавления, выходим из функции
    if (newPaths.isEmpty()) {
        LogSystem.addLog(4, Res.string.updateGameInfoNewPathsEmpty)
        return
    }

    // Формируем обновленное содержимое файла, вставляя новые пути в начало блока с путями
    val updatedContent = buildString {
        append(content.substring(0, searchBlockStart + 1)) // Все до открывающей скобки блока
        append("\n$newPaths\n")                           // Добавляем новые пути в начало блока
        append(content.substring(searchBlockStart + 1, searchBlockEnd)) // Существующие пути
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





// Функция для замены содержимого файла gameinfo.txt на сохраненное в преференциях
fun replaceGameInfoFile(gameInfoFilePath: String) {
    // Загрузить сохраненное содержимое из преференций
    val savedContent = loadGameInfoContent()

    if (savedContent == null) {
        LogSystem.addLog(1, Res.string.replaceGameInfoFileSavedContentNull)

    }

    val gameInfoFile = File(gameInfoFilePath)

    if (gameInfoFile.exists() && gameInfoFile.isFile) {
        // Записываем новое содержимое в файл
        if (savedContent != null) {
            gameInfoFile.writeText(savedContent)
        }
        LogSystem.addLog(3, Res.string.replaceGameInfoFileDone)
    } else {
        LogSystem.addLog(1, Res.string.replaceGameInfoFileFailPath, gameInfoFilePath)
    }
}