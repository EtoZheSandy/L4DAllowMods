package su.afk.l4d2.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.author_create
import kotlinproject.composeapp.generated.resources.gameAddonsNotSelect
import kotlinproject.composeapp.generated.resources.gamePathNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
import su.afk.l4d2.domain.model.AddonInfo
import su.afk.l4d2.domain.service.AddonService
import su.afk.l4d2.domain.service.GameInfoService
import su.afk.l4d2.presenter.setting.WorkshopFolderResult
import su.afk.l4d2.presenter.setting.findWorkshopFolder

class MainViewModel : ViewModel() {

    private val addonService = AddonService()
    private val gameInfoService = GameInfoService()

    private val _state = MutableStateFlow(MainState.State())
    val state: StateFlow<MainState.State> = _state.asStateFlow()

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

    private fun sortAddons(isFilterAsc: Boolean) {
        _state.update { currentState ->
            currentState.copy(
                sortedAddonList = currentState.addonInfoList?.sortedWith(
                    compareBy<AddonInfo> { addon ->
                        val isEnabled = currentState.addonEnabledList?.contains(addon) == true
                        if (isFilterAsc) !isEnabled else isEnabled
                    }
                )
            )
        }
    }

    private fun showListAddons() {
        _state.update { currentState ->
            currentState.copy(showListAddons = !currentState.showListAddons)
        }
    }

    private fun folderSelected(folderPath: String) {
        viewModelScope.launch {
            val pathWorkshop = withContext(Dispatchers.IO) {
                findWorkshopFolder(folderPath)
            }

            when (pathWorkshop) {
                is WorkshopFolderResult.Failure -> {
                    _state.update { currentState ->
                        currentState.copy(errorMessage = pathWorkshop.message)
                    }
                    LogSystem.addLog(1, pathWorkshop.message)
                }

                is WorkshopFolderResult.Success -> {
                    withContext(Dispatchers.IO) {
                        saveFolderPath(pathWorkshop.workshopPath)
                    }
                    _state.update { currentState ->
                        currentState.copy(
                            selectedFolderPath = pathWorkshop.workshopPath,
                            errorMessage = null
                        )
                    }
                }
            }
        }
    }

    private fun setAutoHideMods(enabled: Boolean? = null) {
        _state.update { currentState ->
            currentState.copy(autoHideMods = enabled ?: currentState.autoHideMods)
        }

        val autoHideSettings = _state.value.autoHideMods to _state.value.hideAfterSeconds
        viewModelScope.launch(Dispatchers.IO) {
            saveAutoHideMods(autoHideSettings)
        }
    }

    private fun loadFiles() {
        viewModelScope.launch {
            val loadedState = withContext(Dispatchers.IO) {
                val savedPath = loadFolderPath()
                val addonEnabled = loadEnableAddonsList()
                val autoHideSettings = loadAutoHideMods()

                MainState.State(
                    selectedFolderPath = savedPath,
                    addonEnabledList = addonEnabled,
                    autoHideMods = autoHideSettings?.first ?: false,
                    hideAfterSeconds = autoHideSettings?.second ?: 30
                )
            }

            _state.update { currentState ->
                currentState.copy(
                    selectedFolderPath = loadedState.selectedFolderPath,
                    addonEnabledList = loadedState.addonEnabledList,
                    autoHideMods = loadedState.autoHideMods,
                    hideAfterSeconds = loadedState.hideAfterSeconds
                )
            }
        }
    }

    private fun clickMods(mods: AddonInfo) {
        val currentList = _state.value.addonEnabledList.orEmpty()
        val updatedList = if (currentList.contains(mods)) {
            currentList.minus(mods)
        } else {
            currentList.plus(mods)
        }

        _state.update { currentState ->
            currentState.copy(addonEnabledList = updatedList)
        }
        viewModelScope.launch(Dispatchers.IO) {
            saveEnableAddonsList(updatedList)
        }
    }

    private fun modGameInfo() {
        val selectedFolderPath = _state.value.selectedFolderPath
        if (selectedFolderPath == null) {
            LogSystem.addLog(1, Res.string.gamePathNull)
            return
        }

        val enabledAddons = _state.value.addonEnabledList.orEmpty()
        if (enabledAddons.isEmpty()) {
            LogSystem.addLog(1, Res.string.gameAddonsNotSelect)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val gameinfo = gameInfoService.findGameInfo(selectedFolderPath)
            if (gameinfo != null) {
                val (path, content) = gameinfo

                val savedContent = loadGameInfoContent()
                if (savedContent == null) {
                    saveGameInfoContent(content)
                }

                addonService.prepareAddons(
                    addons = enabledAddons,
                    workshopFolderPath = selectedFolderPath
                )
                gameInfoService.updateGameInfo(addons = enabledAddons, gameInfoFilePath = path)
            }
        }
    }

    private fun defGameInfo() {
        val selectedFolderPath = _state.value.selectedFolderPath
        if (selectedFolderPath == null) {
            LogSystem.addLog(1, Res.string.gamePathNull)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val gameinfo = gameInfoService.findGameInfo(selectedFolderPath)
            if (gameinfo != null) {
                val (path, content) = gameinfo
                gameInfoService.restoreGameInfo(path)
            }
        }
    }

    private fun readVPKFiles() {
        val selectedFolderPath = _state.value.selectedFolderPath
        if (selectedFolderPath == null) {
            LogSystem.addLog(1, Res.string.gamePathNull)
            return
        }

        _state.update { currentState ->
            currentState.copy(loadingAddonInfo = true)
        }
        viewModelScope.launch {
            try {
                val addons = withContext(Dispatchers.IO) {
                    addonService.loadAddons(selectedFolderPath)
                }
                _state.update { currentState ->
                    currentState.copy(addonInfoList = addons, sortedAddonList = null)
                }
            } finally {
                _state.update { currentState ->
                    currentState.copy(loadingAddonInfo = false)
                }
            }
        }
    }

    private fun updateGameInfo() {
        val selectedFolderPath = _state.value.selectedFolderPath
        if (selectedFolderPath == null) {
            LogSystem.addLog(1, Res.string.gamePathNull)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val gameinfo = gameInfoService.findGameInfo(selectedFolderPath)
            if (gameinfo != null) {
                val (path, content) = gameinfo
                saveGameInfoContent(content)
            }
        }
    }

    private fun clearCache() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                clearPreferences()
            }
            _state.value = MainState.State()
        }
    }

    private fun clearCacheAddons() {
        val selectedFolderPath = _state.value.selectedFolderPath
        if (selectedFolderPath == null) {
            LogSystem.addLog(1, Res.string.gamePathNull)
            return
        }

        val enabledAddons = _state.value.addonEnabledList.orEmpty()
        if (enabledAddons.isEmpty()) {
            LogSystem.addLog(1, Res.string.gameAddonsNotSelect)
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            addonService.clearPreparedAddons(
                addons = enabledAddons,
                workshopFolderPath = selectedFolderPath
            )
        }
    }

    private fun onProcessStarted() {
        if (_state.value.autoHideMods) {
            viewModelScope.launch {
                delay(_state.value.hideAfterSeconds * 1000L)
                defGameInfo()
            }
        }
    }

    private fun onProcessStopped() {
        if (_state.value.autoHideMods) {
            modGameInfo()
        }
    }
}
