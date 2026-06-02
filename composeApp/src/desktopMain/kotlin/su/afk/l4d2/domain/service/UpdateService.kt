package su.afk.l4d2.domain.service

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import su.afk.l4d2.domain.model.UpdateCheckState
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class UpdateService(
    private val httpClient: HttpClient = HttpClient.newHttpClient(),
    private val json: Json = Json { ignoreUnknownKeys = true }
) {
    fun checkLatestRelease(currentVersion: String): UpdateCheckState {
        return try {
            val request = HttpRequest.newBuilder()
                .uri(URI.create(LATEST_RELEASE_URL))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "L4DAllowMods")
                .GET()
                .build()

            val response = httpClient.send(request, HttpResponse.BodyHandlers.ofString())
            if (response.statusCode() !in 200..299) {
                return UpdateCheckState.Error("GitHub returned HTTP ${response.statusCode()}")
            }

            val release = json.decodeFromString<GitHubRelease>(response.body())
            val latestVersion = release.tagName.trim()
            val comparison = compareVersions(currentVersion, latestVersion)
                ?: return UpdateCheckState.Error("Unsupported release version: $latestVersion")

            if (comparison < 0) {
                UpdateCheckState.UpdateAvailable(
                    currentVersion = currentVersion,
                    latestVersion = latestVersion,
                    releaseName = release.name,
                    releaseUrl = release.htmlUrl
                )
            } else {
                UpdateCheckState.UpToDate(currentVersion = currentVersion)
            }
        } catch (error: Exception) {
            UpdateCheckState.Error(error.message ?: error::class.simpleName.orEmpty())
        }
    }

    private fun compareVersions(currentVersion: String, latestVersion: String): Int? {
        val current = parseVersion(currentVersion) ?: return null
        val latest = parseVersion(latestVersion) ?: return null

        return current.zip(latest)
            .firstOrNull { (currentPart, latestPart) -> currentPart != latestPart }
            ?.let { (currentPart, latestPart) -> currentPart.compareTo(latestPart) }
            ?: 0
    }

    private fun parseVersion(rawVersion: String): List<Int>? {
        val normalized = rawVersion.trim().removePrefix("v").removePrefix("V")
        val match = VERSION_REGEX.matchEntire(normalized) ?: return null

        return listOf(
            match.groupValues[1].toInt(),
            match.groupValues[2].toInt(),
            match.groupValues[3].toInt()
        )
    }

    @Serializable
    private data class GitHubRelease(
        @SerialName("tag_name") val tagName: String,
        @SerialName("html_url") val htmlUrl: String,
        val name: String? = null
    )

    private companion object {
        const val LATEST_RELEASE_URL = "https://api.github.com/repos/EtoZheSandy/L4DAllowMods/releases/latest"
        val VERSION_REGEX = Regex("""(\d+)\.(\d+)\.(\d+)(?:[-+].*)?""")
    }
}
