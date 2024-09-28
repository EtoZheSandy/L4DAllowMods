package su.afk.l4d2.presenter.setting

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.findWorkshopFolderFailPath
import kotlinproject.composeapp.generated.resources.findWorkshopFolderFailWorkshopFolder
import org.jetbrains.compose.resources.StringResource
import java.io.File

// Сепарированный класс для результата поиска
sealed class WorkshopFolderResult {
    data class Success(val workshopPath: String) : WorkshopFolderResult()
    data class Failure(val message: StringResource) : WorkshopFolderResult()
}

// Функция для проверки валидности папки игры
fun isValidGameFolder(path: String): Boolean {
    val gameFolder = File(path)
    return gameFolder.exists() && gameFolder.isDirectory
}

// Основная функция для поиска папки workshop
fun findWorkshopFolder(path: String): WorkshopFolderResult {
    val gameFolder = File(path)

    if (!isValidGameFolder(path)) {
        return WorkshopFolderResult.Failure(Res.string.findWorkshopFolderFailPath)
    }

    // Нормализация пути
    val normalizedPath = gameFolder.absoluteFile

    // Проверка, указывает ли путь непосредственно на папку workshop
    if (isWorkshopFolder(normalizedPath)) {
        return WorkshopFolderResult.Success(normalizedPath.absolutePath)
    }

    // Возможные относительные пути к папке workshop
    val relativePaths = listOf(
        "left4dead2/addons/workshop",
        "addons/workshop"
    )

    for (relativePath in relativePaths) {
        val workshopFolder = File(normalizedPath, relativePath)
        if (workshopFolder.exists() && workshopFolder.isDirectory) {
            return WorkshopFolderResult.Success(workshopFolder.absolutePath)
        }
    }

    // Рекурсивный поиск папки workshop как резервный вариант
    val workshopFolderRecursive = findFolderRecursively(normalizedPath, listOf("addons", "workshop"))
    if (workshopFolderRecursive != null) {
        return WorkshopFolderResult.Success(workshopFolderRecursive.absolutePath)
    }

    return WorkshopFolderResult.Failure(Res.string.findWorkshopFolderFailWorkshopFolder)
}

// Вспомогательная функция для проверки, является ли папка workshop
private fun isWorkshopFolder(folder: File): Boolean {
    return folder.name.equals("workshop", ignoreCase = true) &&
            folder.parentFile?.name.equals("addons", ignoreCase = true)
}

// Рекурсивная функция для поиска папки по сегментам пути
private fun findFolderRecursively(startDir: File, pathSegments: List<String>): File? {
    if (pathSegments.isEmpty()) return startDir

    val currentSegment = pathSegments.first()
    val remainingSegments = pathSegments.drop(1)

    val dirs = startDir.listFiles { file -> file.isDirectory && file.name.equals(currentSegment, ignoreCase = true) } ?: return null

    for (dir in dirs) {
        val result = findFolderRecursively(dir, remainingSegments)
        if (result != null) {
            return result
        }
    }

    return null
}