package su.afk.l4d2

import org.jetbrains.compose.resources.StringResource
import su.afk.l4d2.utils.AddonInfo

class MainState {
    data class State(
        val selectedFolderPath: String? = null,
        val errorMessage: StringResource? = null,
        val loadingAddonInfo: Boolean = false,
        val addonInfoList: List<AddonInfo>? = null,
        val addonEnabledList: List<AddonInfo>? = null,
        val showListAddons: Boolean = false,
    )

    sealed class Event {
        // выбор папки с игрой
        data class FolderSelected(val folderPath: String) : Event()
        // загрузка из сохранения пути к игре
        object LoadFiles : Event()
        // выбор модов вкл/выкл
        data class ClickMods(val mods: AddonInfo) : Event()
        // замена файла gameinfo на модовый
        object ModGameInfo : Event()
        // замена файла gameinfo на стандартный
        object DefGameInfo : Event()
        // читаем файлы с модами из игровой директории
        object ReadVPKFiles : Event()
        // удаление настроек
        object ClearCache : Event()
        // удаление кэш модов
        object ClearCacheAddons : Event()
        // обновление файла gameinfo в кэше у себя
        object UpdateGameInfo : Event()
        // флаг для отображения списка модов
        object ShowListAddons : Event()
    }
}