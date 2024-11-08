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
        val sortedAddonList: List<AddonInfo>? = null,
        val autoHideMods: Boolean = false,
        val hideAfterSeconds: Int = 5
    )

    sealed class Event {
        // выбор папки с игрой
        data class FolderSelected(val folderPath: String) : Event()
        // загрузка из сохранения пути к игре
        object LoadFiles : Event()
        // выбор addons вкл/выкл
        data class ClickMods(val mods: AddonInfo) : Event()
        // замена файла gameinfo на модовый
        object ModGameInfo : Event()
        // замена файла gameinfo на стандартный
        object DefGameInfo : Event()
        // читаем файлы с модами из игровой директории
        object ReadVPKFiles : Event()
        // удаление настроек
        object ClearCache : Event()
        // удаление кэш addons
        object ClearCacheAddons : Event()
        // обновление файла gameinfo в кэше у себя
        object UpdateGameInfo : Event()
        // флаг для отображения списка addons
        object ShowListAddons : Event()
        // сортировка addons
        data class SortAddons(val isFilterAsc: Boolean) : Event()
        // процесс запущен
        object ProcessStarted : Event()
        // процесс закрыт
        object ProcessStopped : Event()
        // изменение чекбокса на автоскрытие
        data class SetAutoHideMods(val enabled: Boolean) : Event()
    }
}