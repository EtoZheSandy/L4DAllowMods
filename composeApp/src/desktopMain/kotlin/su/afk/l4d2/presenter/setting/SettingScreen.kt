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
import su.afk.l4d2.MainState
import su.afk.l4d2.MainViewModel
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView

@Composable
fun SettingsScreen(
    viewModel: MainViewModel
) {
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.Start
    ) {
        viewModel.state.errorMessage?.let { message ->
            Text(text = message, color = MaterialTheme.colors.error)
        }

        Text("Выберите папку с игрой", color = MaterialTheme.colors.onSurface)

        Button(
            onClick = {
                val chooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory)
                chooser.dialogTitle = "Выберите папку с Left 4 Dead"
                chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                chooser.isAcceptAllFileFilterUsed = false
                val returnValue = chooser.showOpenDialog(null)
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    val selectedFolder = chooser.selectedFile.absolutePath
                    viewModel.handlerEvents(MainState.Event.FolderSelected(selectedFolder))
                }
            },
            modifier = Modifier.padding(vertical = 10.dp)
        ) {
            if (viewModel.state.selectedFolderPath == null) {
                Text("Выбрать папку")
            } else {
                Text("Изменить папку")
            }
        }

        viewModel.state.selectedFolderPath?.let { path ->
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "Сейчас выбрана папка:",
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
            viewModel.handlerEvents(MainState.Event.UpdateGameInfo)
        }) {
            Text("Обновить gameinfo в кэше tools")
        }

        Button(onClick = {
            viewModel.handlerEvents(MainState.Event.ClearCacheAddons)
        }) {
            Text("Удалить кэш addons")
        }

        Button(onClick = {
            viewModel.handlerEvents(MainState.Event.ClearCache)
        }) {
            Text("Удалить сохраненные настройки")
        }
    }

}