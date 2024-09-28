package su.afk.l4d2.utils

import su.afk.l4d2.data.LogSystem
import su.afk.l4d2.data.loadGameInfoContent
import java.io.File

// Функция для обрезки двух последних папок из пути
fun findGameInfo(originalPath: String): Pair<String, String>? {
    // Разделение пути на компоненты
    val pathComponents = originalPath.split(File.separator)
    if (pathComponents.size < 3) {
        LogSystem.addLog(1, "Файл gameinfo.txt не найден по указанному пути: $originalPath")
        return null
    }

    // Создаем новый путь без двух последних папок
    val trimmedPath = pathComponents.dropLast(2).joinToString(File.separator)
    println("trimmedPath $trimmedPath")

    // Путь к файлу gameinfo.txt
    val gameInfoFilePath = File(trimmedPath, "gameinfo.txt")
    println("new path $gameInfoFilePath")

    // Проверяем существование файла и возвращаем путь и содержимое
    return if (gameInfoFilePath.exists() && gameInfoFilePath.isFile) {
        gameInfoFilePath.absolutePath to gameInfoFilePath.readText() // Возвращаем путь и содержимое как пару
    } else {
        LogSystem.addLog(1, "Файл gameinfo.txt не существует либо не найден по указанному пути: $originalPath")
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
            LogSystem.addLog(2, "Файл ${addon.filename} не найден по пути $basePath")
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
            LogSystem.addLog(4, "Файл ${addon.filename} успешно скопирован и переименован в ${newFile.absolutePath}")
        } catch (e: Exception) {
            LogSystem.addLog(2, "Ошибка при копировании файла: ${addon.filename}| ${e.message}")
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
                LogSystem.addLog(4, "Папка ${folderPath.absolutePath} успешно удалена.")
            } catch (e: Exception) {
                LogSystem.addLog(2, "Ошибка при удалении папки ${folderPath.absolutePath}: ${e.message}")
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
                    LogSystem.addLog(4, "Папка ${folder.absolutePath} успешно удалена.")
                } catch (e: Exception) {
                    LogSystem.addLog(2, "Ошибка при удалении папки ${folder.absolutePath}: ${e.message}")
                }
            } else {
                LogSystem.addLog(4, "Папка ${folder.absolutePath} не была удалена, так как содержит другие папки.")
            }
        }
    }

    LogSystem.addLog(3, "Кэш Addons успешно удален")
}

// Функция для добавления путей в блок SearchPaths в gameinfo.txt
fun updateGameInfoFile(addons: List<AddonInfo>, gameInfoFilePath: String) {
    // Читаем содержимое файла gameinfo.txt
    val gameInfoFile = File(gameInfoFilePath)

    if (!gameInfoFile.exists() || !gameInfoFile.isFile) {
        LogSystem.addLog(1, "Не удалось заменить файл gameinfo.txt не найден по указанному пути: $gameInfoFilePath")
        return
    }

    val content = gameInfoFile.readText()

    // Ищем место для вставки новых путей в блок SearchPaths
    val searchPathsStart = content.indexOf("SearchPaths")
    if (searchPathsStart == -1) {
        LogSystem.addLog(4, "Блок SearchPaths не найден в файле gameinfo.txt")
        return
    }

    // Ищем закрывающую скобку для блока SearchPaths
    val searchPathsEnd = content.indexOf("}", searchPathsStart)
    if (searchPathsEnd == -1) {
        LogSystem.addLog(4, "Закрывающая скобка для блока SearchPaths не найдена в файле gameinfo.txt")
        return
    }

    // Извлекаем существующий блок SearchPaths
    val existingPathsBlock = content.substring(searchPathsStart, searchPathsEnd)

    // Генерируем новые строки для добавления в SearchPaths, исключая дубли
    val newPaths = addons.map { addon ->
        """            Game                left4dead2\addons\workshop\${addon.filename.substringBeforeLast(".")}"""
    }.filterNot { newPath ->
        existingPathsBlock.contains(newPath) // Проверяем, есть ли путь уже в блоке
    }.joinToString(separator = "\n")

    // Если нет новых путей для добавления, выходим из функции
    if (newPaths.isEmpty()) {
        LogSystem.addLog(4, "Нет новых путей для добавления в gameinfo.txt")
        return
    }

    // Формируем обновленное содержимое файла, вставляя новые пути в блок SearchPaths
    val updatedContent = buildString {
        append(content.substring(0, searchPathsEnd)) // Все до закрывающей скобки блока SearchPaths
        append("\n$newPaths\n")                      // Добавляем новые пути
        append(content.substring(searchPathsEnd))    // Все после закрывающей скобки блока SearchPaths
    }

    // Сохраняем обновленное содержимое обратно в файл
    try {
        gameInfoFile.writeText(updatedContent)
        LogSystem.addLog(3, "Файл gameinfo.txt успешно обновлен.")
    } catch (e: Exception) {
        LogSystem.addLog(1, "Ошибка при сохранении файла gameinfo.txt: ${e.message}")
    }
}


// Функция для замены содержимого файла gameinfo.txt на сохраненное в преференциях
fun replaceGameInfoFile(gameInfoFilePath: String): Pair<String, Boolean> {
    // Загрузить сохраненное содержимое из преференций
    val savedContent = loadGameInfoContent()

    if (savedContent == null) {
        LogSystem.addLog(1, "Нет сохраненного содержимого для замены gameinfo.txt")
        return Pair("Нет сохраненного содержимого для замены", false)
    }

    println("savedContent = $savedContent")
    val gameInfoFile = File(gameInfoFilePath)

    if (gameInfoFile.exists() && gameInfoFile.isFile) {
        // Записываем новое содержимое в файл
        gameInfoFile.writeText(savedContent)
        LogSystem.addLog(3, "Файл gameinfo.txt успешно восстановлен")
        return Pair("Файл gameinfo.txt успешно восстановлен", true)
    } else {
        LogSystem.addLog(1, "Файл gameinfo.txt не найден по указанному пути: $gameInfoFilePath")
        return Pair("Файл gameinfo.txt не найден по указанному пути: $gameInfoFilePath", false)
    }
}