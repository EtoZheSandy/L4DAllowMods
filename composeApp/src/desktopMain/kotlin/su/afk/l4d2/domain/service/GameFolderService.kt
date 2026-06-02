package su.afk.l4d2.domain.service

import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.findWorkshopFolderFailPath
import kotlinproject.composeapp.generated.resources.findWorkshopFolderFailWorkshopFolder
import org.jetbrains.compose.resources.StringResource
import java.io.File

private const val GAME_FOLDER_NAME = "Left 4 Dead 2"
private const val STEAM_APPS_PATH = "steamapps/common/$GAME_FOLDER_NAME"

sealed class WorkshopFolderResult {
    data class Success(val workshopPath: String) : WorkshopFolderResult()
    data class Failure(val message: StringResource) : WorkshopFolderResult()
}

class GameFolderService {

    fun isAutoSearchSupported(): Boolean {
        return isWindows()
    }

    fun findInstalledGameFolder(): String? {
        if (!isAutoSearchSupported()) return null

        val candidates = buildGameFolderCandidates()
        return candidates.firstOrNull { folder ->
            folder.exists() && folder.isDirectory
        }?.absolutePath
    }

    fun findWorkshopFolder(path: String): WorkshopFolderResult {
        val sanitizedPath = sanitizeFolderPath(path)
        val gameFolder = File(sanitizedPath)

        if (!isValidGameFolder(sanitizedPath)) {
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

        val workshopFolderRecursive = findFolderRecursively(normalizedPath, listOf("addons", "workshop"))
        if (workshopFolderRecursive != null) {
            return WorkshopFolderResult.Success(workshopFolderRecursive.absolutePath)
        }

        return WorkshopFolderResult.Failure(Res.string.findWorkshopFolderFailWorkshopFolder)
    }

    private fun buildGameFolderCandidates(): List<File> {
        val roots = File.listRoots().toList()
        val steamRoots = roots.flatMap { root ->
            listOf(
                File(root, "Program Files (x86)/Steam"),
                File(root, "Program Files/Steam"),
                File(root, "Steam"),
                File(root, "SteamLibrary")
            )
        }

        val steamInstallCandidates = listOf(File("C:/Program Files (x86)/Steam")) + steamRoots

        val libraryFolders = steamInstallCandidates
            .flatMap { steamRoot -> findSteamLibraries(steamRoot) }

        val directCandidates = steamRoots.map { steamRoot ->
            File(steamRoot, STEAM_APPS_PATH)
        }

        return (listOf(File("C:/Program Files (x86)/Steam/$STEAM_APPS_PATH")) +
                libraryFolders.map { library -> File(library, STEAM_APPS_PATH) } +
                directCandidates)
            .distinctBy { it.absolutePath.lowercase() }
    }

    private fun findSteamLibraries(steamRoot: File): List<File> {
        val libraryFoldersFile = File(steamRoot, "steamapps/libraryfolders.vdf")
        if (!libraryFoldersFile.exists() || !libraryFoldersFile.isFile) {
            return emptyList()
        }

        val content = runCatching { libraryFoldersFile.readText() }.getOrNull() ?: return emptyList()
        val parsedPaths = LIBRARY_PATH_REGEX.findAll(content)
            .map { match -> match.groupValues[1].replace("\\\\", "\\") }
            .map(::File)
            .toList()

        return (listOf(steamRoot) + parsedPaths)
            .filter { it.exists() && it.isDirectory }
            .distinctBy { it.absolutePath.lowercase() }
    }

    private fun isValidGameFolder(path: String): Boolean {
        val gameFolder = File(path)
        return gameFolder.exists() && gameFolder.isDirectory
    }

    private fun sanitizeFolderPath(path: String): String {
        val trimmedPath = path.trim()
        return if (
            trimmedPath.length >= 2 &&
            ((trimmedPath.first() == '"' && trimmedPath.last() == '"') ||
                    (trimmedPath.first() == '\'' && trimmedPath.last() == '\''))
        ) {
            trimmedPath.substring(1, trimmedPath.lastIndex).trim()
        } else {
            trimmedPath
        }
    }

    private fun isWorkshopFolder(folder: File): Boolean {
        return folder.name.equals("workshop", ignoreCase = true) &&
                folder.parentFile?.name.equals("addons", ignoreCase = true)
    }

    private fun findFolderRecursively(startDir: File, pathSegments: List<String>): File? {
        if (pathSegments.isEmpty()) return startDir

        val currentSegment = pathSegments.first()
        val remainingSegments = pathSegments.drop(1)

        val dirs = startDir.listFiles { file ->
            file.isDirectory && file.name.equals(currentSegment, ignoreCase = true)
        } ?: return null

        for (dir in dirs) {
            val result = findFolderRecursively(dir, remainingSegments)
            if (result != null) {
                return result
            }
        }

        return null
    }

    private fun isWindows(): Boolean {
        return System.getProperty("os.name")
            .orEmpty()
            .contains("windows", ignoreCase = true)
    }

    private companion object {
        val LIBRARY_PATH_REGEX = """"path"\s+"([^"]+)"""".toRegex()
    }
}
