package su.afk.l4d2.presenter.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.addonsOff
import kotlinproject.composeapp.generated.resources.addonsOn
import kotlinproject.composeapp.generated.resources.autoHideMods
import kotlinproject.composeapp.generated.resources.gameAddonsNotSelect
import kotlinproject.composeapp.generated.resources.gameDoneFAQ
import kotlinproject.composeapp.generated.resources.gamePathNull
import org.jetbrains.compose.resources.stringResource
import su.afk.l4d2.MainState
import su.afk.l4d2.MainViewModel


@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    Column {
        if (viewModel.state.selectedFolderPath == null) {
            Text(
                stringResource(Res.string.gamePathNull),
                color = MaterialTheme.colors.error
            )
        } else if (viewModel.state.addonEnabledList == null) {
            Text(
                stringResource(Res.string.gameAddonsNotSelect),
                color = MaterialTheme.colors.error
            )
        } else {
            Text(
                stringResource(Res.string.gameDoneFAQ),
                color = MaterialTheme.colors.onSurface
            )
            Row(modifier = Modifier.padding(top = 16.dp)) {
                Button(
                    onClick = { viewModel.handlerEvents(MainState.Event.ModGameInfo) },
                    modifier = Modifier.padding(end = 32.dp)
                ) {
                    Text(stringResource(Res.string.addonsOn))
                }

                Button(onClick = { viewModel.handlerEvents(MainState.Event.DefGameInfo) }) {
                    Text(stringResource(Res.string.addonsOff))
                }
            }

            // Switch для авто-скрытия модов
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch (
                    checked = viewModel.state.autoHideMods,
                    onCheckedChange = { checked ->
                        viewModel.handlerEvents(MainState.Event.SetAutoHideMods(checked))
                    }
                )
                Text(
                    stringResource(Res.string.autoHideMods),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
