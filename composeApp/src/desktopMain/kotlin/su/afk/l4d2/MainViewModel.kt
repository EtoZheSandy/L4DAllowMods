package su.afk.l4d2

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.author_create
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import su.afk.l4d2.data.LogSystem
import su.afk.l4d2.data.clearPreferences
import su.afk.l4d2.data.loadAutoHideMods
import su.afk.l4d2.data.loadEnableAddonsList
import su.afk.l4d2.data.loadFolderPath
import su.afk.l4d2.data.loadGameInfoContent
import su.afk.l4d2.data.saveAutoHideMods
import su.afk.l4d2.data.saveEnableAddonsList
import su.afk.l4d2.data.saveFolderPath
import su.afk.l4d2.data.saveGameInfoContent
import su.afk.l4d2.presenter.setting.WorkshopFolderResult
import su.afk.l4d2.presenter.setting.findWorkshopFolder
import su.afk.l4d2.utils.AddonInfo
import su.afk.l4d2.utils.deleteAddonFolders
import su.afk.l4d2.utils.findGameInfo
import su.afk.l4d2.utils.loadAddonInfo
import su.afk.l4d2.utils.processAddonFile
import su.afk.l4d2.utils.replaceGameInfoFile
import su.afk.l4d2.utils.updateGameInfoFile


class MainViewModel : ViewModel() {

    var state by mutableStateOf(MainState.State(null, null))
        private set

    init {
        loadFiles()

        LogSystem.addLog(priority = 1, message = Res.string.author_create)
    }

    fun handlerEvents(event: MainState.Event) {
        when (event) {
            is MainState.Event.FolderSelected -> folderSelected(event.folderPath)
            is MainState.Event.LoadFiles -> loadFiles()
            is MainState.Event.ClickMods -> clickMods(event.mods)
            MainState.Event.DefGameInfo -> defGameInfo()
            MainState.Event.ModGameInfo -> modGameInfo()
            MainState.Event.ReadVPKFiles -> readVPKFiles()
            MainState.Event.ClearCache -> clearCache()
            MainState.Event.ClearCacheAddons -> clearCacheAddons()
            MainState.Event.UpdateGameInfo -> updateGameInfo()
            MainState.Event.ShowListAddons -> showListAddons()
            is MainState.Event.SortAddons -> sortAddons(event.isFilterAsc)
            is MainState.Event.ProcessStarted -> onProcessStarted()
            is MainState.Event.ProcessStopped -> onProcessStopped()
            is MainState.Event.SetAutoHideMods -> setAutoHideMods(enabled = event.enabled)
        }
    }

    /** Функция для сортировки, вызывается при клике на кнопку сортировки */
    private fun sortAddons(isFilterAsc: Boolean) {
        state = state.copy(
            sortedAddonList = state.addonInfoList?.sortedWith(
                compareBy<AddonInfo> { addon ->
                    val isEnabled = state.addonEnabledList?.contains(addon) == true
                    if (isFilterAsc) !isEnabled else isEnabled
                }
            )
        )
    }

    /** отображение списка модов */
    private fun showListAddons() {
        state = state.copy(showListAddons = !state.showListAddons)
    }

    /** путь до папки с игрой
     * G:\SteamLibrary\steamapps\common\Left 4 Dead 2 */
    private fun folderSelected(folderPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val pathWorkshop = findWorkshopFolder(folderPath) // проверка
            when (pathWorkshop) {
                is WorkshopFolderResult.Failure -> {
                    state = state.copy(errorMessage = pathWorkshop.message)
                    LogSystem.addLog(1, pathWorkshop.message)
                }

                is WorkshopFolderResult.Success -> {
                    saveFolderPath(pathWorkshop.workshopPath)
                    state = state.copy(
                        selectedFolderPath = pathWorkshop.workshopPath,
                        errorMessage = null
                    )
                }
            }
        }
    }

    /** авто скрытие модов */
    private fun setAutoHideMods(enabled: Boolean? = null) {
        state = state.copy(
            autoHideMods = enabled ?: state.autoHideMods,
        )
        saveAutoHideMods(state.autoHideMods to state.hideAfterSeconds)
    }

    /** загрузка сохраненного пути до игровой директории
     *  и списка включенных модов */
    private fun loadFiles() {
        val savedPath = loadFolderPath()
        val addonEnabled = loadEnableAddonsList()
        val autoHideSettings = loadAutoHideMods()

        val autoHideMods = autoHideSettings?.first ?: false
        val hideAfterSeconds = autoHideSettings?.second ?: 30

        state = state.copy(selectedFolderPath = savedPath, addonEnabledList = addonEnabled,
            autoHideMods = autoHideMods, hideAfterSeconds = hideAfterSeconds)
    }

    /** выбор модов вкл/выкл для gameinfo */
    private fun clickMods(mods: AddonInfo) {
        // проверяем есть ли он в addonEnabledList
        val currentList = state.addonEnabledList ?: emptyList()
        if (currentList.contains(mods)) {
            state = state.copy(addonEnabledList = currentList.minus(mods))
        } else {
            state = state.copy(addonEnabledList = currentList.plus(mods))
        }
        saveEnableAddonsList(state.addonEnabledList ?: emptyList())
    }

    /** замена файл gameinfo на модовый */
    private fun modGameInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            // Получаем путь до gameinfo.txt
            val gameinfo = findGameInfo(state.selectedFolderPath!!)
            if (gameinfo != null) {
                val (path, content) = gameinfo

                // Загрузить сохраненное содержимое из преференций
                val savedContent = loadGameInfoContent()
                if (savedContent == null) {
                    // Сохраняем содержимое gameinfo
                    saveGameInfoContent(content)
                }

                // копируем файлы в новые папки pack01
                processAddonFile(
                    addons = state.addonEnabledList!!,
                    basePath = state.selectedFolderPath!!
                )

                // а теперь вставляем в gameinfo инфу о модах и папках
                updateGameInfoFile(addons = state.addonEnabledList!!, gameInfoFilePath = path)
            }
        }
    }


    /** замена файла gameinfo на стандартный */
    private fun defGameInfo() {
        viewModelScope.launch(Dispatchers.IO) {
            // Получаем путь до gameinfo.txt
            val gameinfo = findGameInfo(state.selectedFolderPath!!)

            if (gameinfo != null) {
                val (path, content) = gameinfo
                // заменяем файл на стандартный
                replaceGameInfoFile(path)
            }
        }
    }

    /** кнока Показать addons
     * читаем файлы из игровой папки */
    private fun readVPKFiles() {
        state = state.copy(loadingAddonInfo = true)
        viewModelScope.launch(Dispatchers.IO) {
            val addons = loadAddonInfo(state.selectedFolderPath!!)
            state = state.copy(addonInfoList = addons)
            state = state.copy(loadingAddonInfo = false)
        }
    }

    /** обновляем gameinfo включенными модами */
    private fun updateGameInfo() {
        // Получаем путь до gameinfo.txt
        val gameinfo = findGameInfo(state.selectedFolderPath!!)

        if (gameinfo != null) {
            val (path, content) = gameinfo
            // Сохраняем содержимое gameinfo
            saveGameInfoContent(content)
        }
    }

    /** удаление данных из кэша */
    private fun clearCache() {
        viewModelScope.launch(Dispatchers.IO) {
            clearPreferences()
            saveGameInfoContent(null)
            state = state
        }
    }

    /** удаляем скопированные vpk файлы из addons */
    private fun clearCacheAddons() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteAddonFolders(
                addons = state.addonEnabledList!!,
                basePath = state.selectedFolderPath!!
            )
        }
    }

    /** Автозамена файла на модовый */
    private fun onProcessStarted() {
        if(state.autoHideMods) {
            viewModelScope.launch {
                delay(state.hideAfterSeconds * 1000L) // Ждем заданное количество секунд
                defGameInfo() // Выполняем действие после таймера
            }
        }
    }

    /** Автозамена файла на стандартный */
    private fun onProcessStopped() {
        if(state.autoHideMods) {
            modGameInfo()
        }
    }
}