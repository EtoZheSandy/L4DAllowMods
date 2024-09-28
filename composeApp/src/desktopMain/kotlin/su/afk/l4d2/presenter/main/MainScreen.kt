package su.afk.l4d2.presenter.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.afk.l4d2.MainState
import su.afk.l4d2.MainViewModel


@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    Column {
        if (viewModel.state.selectedFolderPath == null) {
            Text(
                "Сначала необходимо выбрать папку с игрой в настройках",
                color = MaterialTheme.colors.error
            )
        } else if (viewModel.state.addonEnabledList == null) {
            Text(
                "Выберите какие Addons необходимо включить в My Addons",
                color = MaterialTheme.colors.error
            )
        } else {
            Text(
                "Приятной игры! Все ответы в FAQ",
                color = MaterialTheme.colors.onSurface
            )
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Button(
                    onClick = { viewModel.handlerEvents(MainState.Event.ModGameInfo) },
                    modifier = Modifier.padding(end = 32.dp)
                ) {
                    Text("Включить Addons")
                }

                Button(onClick = { viewModel.handlerEvents(MainState.Event.DefGameInfo) }) {
                    Text("Выключить Addons")
                }
            }
        }
    }
}
