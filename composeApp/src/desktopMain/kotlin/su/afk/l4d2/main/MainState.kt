package su.afk.l4d2.main

import org.jetbrains.compose.resources.StringResource
import su.afk.l4d2.domain.model.AddonInfo

class MainState {
    data class State(
        val selectedFolderPath: String? = null,
        val errorMessage: StringResource? = null,
        val gameFolderSearchState: GameFolderSearchState = GameFolderSearchState.Idle,
        val loadingAddonInfo: Boolean = false,
        val addonInfoList: List<AddonInfo>? = null,
        val addonEnabledList: List<AddonInfo>? = null,
        val showListAddons: Boolean = false,
        val sortedAddonList: List<AddonInfo>? = null,
        val autoHideMods: Boolean = false,
        val hideAfterSeconds: Int = 5
    )

    sealed class Event {
        data class FolderSelected(val folderPath: String) : Event()
        data class FolderPathEntered(val folderPath: String) : Event()
        object AutoFindGameFolder : Event()
        object LoadFiles : Event()
        data class ClickMods(val mods: AddonInfo) : Event()
        object ModGameInfo : Event()
        object DefGameInfo : Event()
        object ReadVPKFiles : Event()
        object ClearCache : Event()
        object ClearCacheAddons : Event()
        object UpdateGameInfo : Event()
        object ShowListAddons : Event()
        data class SortAddons(val isFilterAsc: Boolean) : Event()
        object ProcessStarted : Event()
        object ProcessStopped : Event()
        data class SetAutoHideMods(val enabled: Boolean) : Event()
    }

    enum class GameFolderSearchState {
        Idle,
        Searching,
        Found,
        NotFound
    }
}
