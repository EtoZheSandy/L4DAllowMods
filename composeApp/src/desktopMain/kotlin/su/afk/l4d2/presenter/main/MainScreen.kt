package su.afk.l4d2.presenter.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.addonsOff
import kotlinproject.composeapp.generated.resources.addonsOn
import kotlinproject.composeapp.generated.resources.addonsWarningEnable
import kotlinproject.composeapp.generated.resources.autoHideMods
import kotlinproject.composeapp.generated.resources.gameAddonsNotSelect
import kotlinproject.composeapp.generated.resources.gameDoneFAQ
import kotlinproject.composeapp.generated.resources.gamePathNull
import org.jetbrains.compose.resources.stringResource
import su.afk.l4d2.main.MainState


@Composable
fun MainScreen(
    state: MainState.State,
    onEvent: (MainState.Event) -> Unit
) {
    Column {
        if (state.selectedFolderPath == null) {
            Text(
                stringResource(Res.string.gamePathNull),
                color = MaterialTheme.colors.error
            )
        } else if (state.addonEnabledList.isNullOrEmpty()) {
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
                    onClick = { onEvent(MainState.Event.ModGameInfo) },
                    modifier = Modifier.padding(end = 32.dp)
                ) {
                    Text(stringResource(Res.string.addonsOn))
                }

                Button(onClick = { onEvent(MainState.Event.DefGameInfo) }) {
                    Text(stringResource(Res.string.addonsOff))
                }
            }

            // Switch для авто-скрытия модов
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch (
                    checked = state.autoHideMods,
                    onCheckedChange = { checked ->
                        onEvent(MainState.Event.SetAutoHideMods(checked))
                    }
                )
                Text(
                    stringResource(Res.string.autoHideMods),
                    color = MaterialTheme.colors.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                stringResource(Res.string.addonsWarningEnable),
                color = MaterialTheme.colors.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
