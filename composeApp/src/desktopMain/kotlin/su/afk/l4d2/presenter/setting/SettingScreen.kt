package su.afk.l4d2.presenter.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.changeFolder
import kotlinproject.composeapp.generated.resources.currentFolder
import kotlinproject.composeapp.generated.resources.selectFolder
import kotlinproject.composeapp.generated.resources.selectGameFolder
import kotlinproject.composeapp.generated.resources.selectGameFolderL4D2
import kotlinproject.composeapp.generated.resources.settingDeleteCacheAddons
import kotlinproject.composeapp.generated.resources.settingDeleteCacheSetting
import kotlinproject.composeapp.generated.resources.settingUpdateGameinfo
import org.jetbrains.compose.resources.stringResource
import su.afk.l4d2.main.MainState
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

@Composable
fun SettingsScreen(
    state: MainState.State,
    onEvent: (MainState.Event) -> Unit
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        state.errorMessage?.let { message ->
            Text(text = stringResource(message), color = MaterialTheme.colors.error)
        }

        Text(stringResource(Res.string.selectGameFolder), color = MaterialTheme.colors.onSurface)

        val selectGameFolderL4D2 = stringResource(Res.string.selectGameFolderL4D2)

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
            },
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            if (state.selectedFolderPath == null) {
                Text(stringResource(Res.string.selectFolder))
            } else {
                Text(stringResource(Res.string.changeFolder))
            }
        }

        state.selectedFolderPath?.let { path ->
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    stringResource(Res.string.currentFolder),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            }
            SelectionContainer {
                Text(
                    text = path,
                    color = MaterialTheme.colors.secondary,
                    fontSize = 19.sp
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Занимает всё свободное пространство

        Button(onClick = {
            onEvent(MainState.Event.UpdateGameInfo)
        }) {
            Text(stringResource(Res.string.settingUpdateGameinfo))
        }

        Button(onClick = {
            onEvent(MainState.Event.ClearCacheAddons)
        }) {
            Text(stringResource(Res.string.settingDeleteCacheAddons))
        }

        Button(onClick = {
            onEvent(MainState.Event.ClearCache)
        }) {
            Text(stringResource(Res.string.settingDeleteCacheSetting))
        }
    }

}
