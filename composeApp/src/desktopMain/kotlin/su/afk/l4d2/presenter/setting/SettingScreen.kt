package su.afk.l4d2.presenter.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.changeFolder
import kotlinproject.composeapp.generated.resources.currentFolder
import kotlinproject.composeapp.generated.resources.currentVersion
import kotlinproject.composeapp.generated.resources.openRelease
import kotlinproject.composeapp.generated.resources.selectFolder
import kotlinproject.composeapp.generated.resources.selectGameFolder
import kotlinproject.composeapp.generated.resources.selectGameFolderL4D2
import kotlinproject.composeapp.generated.resources.setting
import kotlinproject.composeapp.generated.resources.settingDeleteCacheAddons
import kotlinproject.composeapp.generated.resources.settingDeleteCacheSetting
import kotlinproject.composeapp.generated.resources.settingUpdateGameinfo
import kotlinproject.composeapp.generated.resources.updateAvailable
import kotlinproject.composeapp.generated.resources.updateChecking
import kotlinproject.composeapp.generated.resources.updateError
import kotlinproject.composeapp.generated.resources.updateSectionTitle
import kotlinproject.composeapp.generated.resources.updateUpToDate
import org.jetbrains.compose.resources.stringResource
import su.afk.l4d2.BuildConfig
import su.afk.l4d2.domain.model.UpdateCheckState
import su.afk.l4d2.main.MainState
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

@Composable
fun SettingsScreen(
    state: MainState.State,
    onEvent: (MainState.Event) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.setting),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        state.errorMessage?.let { message ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.16f)
            ) {
                Text(
                    text = stringResource(message),
                    modifier = Modifier.padding(14.dp),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        FolderSection(state = state, onEvent = onEvent)
        UpdateStatus(
            updateCheckState = state.updateCheckState,
            onEvent = onEvent
        )
        MaintenanceSection(onEvent = onEvent)
    }
}

@Composable
private fun FolderSection(
    state: MainState.State,
    onEvent: (MainState.Event) -> Unit
) {
    val selectGameFolderL4D2 = stringResource(Res.string.selectGameFolderL4D2)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(Res.string.selectGameFolder),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = {
                    val chooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory)
                    chooser.dialogTitle = selectGameFolderL4D2
                    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                    chooser.isAcceptAllFileFilterUsed = false
                    val returnValue = chooser.showOpenDialog(null)
                    if (returnValue == JFileChooser.APPROVE_OPTION) {
                        val selectedFolder = chooser.selectedFile.absolutePath
                        onEvent(MainState.Event.FolderSelected(selectedFolder))
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.FolderOpen,
                    contentDescription = null
                )
                Text(
                    text = if (state.selectedFolderPath == null) {
                        stringResource(Res.string.selectFolder)
                    } else {
                        stringResource(Res.string.changeFolder)
                    },
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            state.selectedFolderPath?.let { path ->
                Text(
                    text = stringResource(Res.string.currentFolder),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    SelectionContainer {
                        Text(
                            text = path,
                            modifier = Modifier.padding(12.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun UpdateStatus(
    updateCheckState: UpdateCheckState,
    onEvent: (MainState.Event) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.updateSectionTitle),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(Res.string.currentVersion, BuildConfig.VERSION_NAME),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when (updateCheckState) {
                UpdateCheckState.Idle,
                UpdateCheckState.Checking -> {
                    Text(
                        text = stringResource(Res.string.updateChecking),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is UpdateCheckState.UpToDate -> {
                    Text(
                        text = stringResource(Res.string.updateUpToDate),
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                is UpdateCheckState.UpdateAvailable -> {
                    Text(
                        text = stringResource(Res.string.updateAvailable, updateCheckState.latestVersion),
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                    updateCheckState.releaseName?.takeIf { it.isNotBlank() }?.let { releaseName ->
                        Text(
                            text = releaseName,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(
                        onClick = { onEvent(MainState.Event.OpenLatestRelease) },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(Res.string.openRelease),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                is UpdateCheckState.Error -> {
                    Text(
                        text = stringResource(Res.string.updateError, updateCheckState.message),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun MaintenanceSection(onEvent: (MainState.Event) -> Unit) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(onClick = { onEvent(MainState.Event.UpdateGameInfo) }) {
                Icon(
                    imageVector = Icons.Filled.SystemUpdate,
                    contentDescription = null
                )
                Text(
                    text = stringResource(Res.string.settingUpdateGameinfo),
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(onClick = { onEvent(MainState.Event.ClearCacheAddons) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(Res.string.settingDeleteCacheAddons),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                OutlinedButton(onClick = { onEvent(MainState.Event.ClearCache) }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                    Text(
                        text = stringResource(Res.string.settingDeleteCacheSetting),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
