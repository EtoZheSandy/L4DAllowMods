package su.afk.l4d2.domain.model

sealed class UpdateCheckState {
    object Idle : UpdateCheckState()
    object Checking : UpdateCheckState()
    data class UpToDate(val currentVersion: String) : UpdateCheckState()
    data class UpdateAvailable(
        val currentVersion: String,
        val latestVersion: String,
        val releaseName: String?,
        val releaseUrl: String
    ) : UpdateCheckState()
    data class Error(val message: String) : UpdateCheckState()
}
