package su.afk.l4d2.presenter.setting

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.findWorkshopFolderFailPath
import kotlinproject.composeapp.generated.resources.findWorkshopFolderFailWorkshopFolder
import org.jetbrains.compose.resources.StringResource
import java.io.File

sealed class WorkshopFolderResult {
    data class Success(val workshopPath: String) : WorkshopFolderResult()
    data class Failure(val message: StringResource) : WorkshopFolderResult()
}

fun isValidGameFolder(path: String): Boolean {
    val gameFolder = File(path)
    return gameFolder.exists() && gameFolder.isDirectory
}

fun findWorkshopFolder(path: String): WorkshopFolderResult {
    val gameFolder = File(path)

    if (!isValidGameFolder(path)) {
        return WorkshopFolderResult.Failure(Res.string.findWorkshopFolderFailPath)
    }

    val normalizedPath = gameFolder.absoluteFile

    if (isWorkshopFolder(normalizedPath)) {
        return WorkshopFolderResult.Success(normalizedPath.absolutePath)
    }

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

    // Fallback for custom Steam library layouts.
    val workshopFolderRecursive = findFolderRecursively(normalizedPath, listOf("addons", "workshop"))
    if (workshopFolderRecursive != null) {
        return WorkshopFolderResult.Success(workshopFolderRecursive.absolutePath)
    }

    return WorkshopFolderResult.Failure(Res.string.findWorkshopFolderFailWorkshopFolder)
}

private fun isWorkshopFolder(folder: File): Boolean {
    return folder.name.equals("workshop", ignoreCase = true) &&
            folder.parentFile?.name.equals("addons", ignoreCase = true)
}

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
