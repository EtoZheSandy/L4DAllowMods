package su.afk.l4d2.presenter.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
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
import kotlinproject.composeapp.generated.resources.main
import org.jetbrains.compose.resources.stringResource
import su.afk.l4d2.main.MainState

@Composable
fun MainScreen(
    state: MainState.State,
    onEvent: (MainState.Event) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(Res.string.main),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                StatusText(state = state)

                if (state.selectedFolderPath != null && !state.addonEnabledList.isNullOrEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onEvent(MainState.Event.ModGameInfo) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PowerSettingsNew,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text(stringResource(Res.string.addonsOn))
                        }

                        OutlinedButton(
                            onClick = { onEvent(MainState.Event.DefGameInfo) },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Restore,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.padding(4.dp))
                            Text(stringResource(Res.string.addonsOff))
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Switch(
                                checked = state.autoHideMods,
                                onCheckedChange = { checked ->
                                    onEvent(MainState.Event.SetAutoHideMods(checked))
                                }
                            )
                            Text(
                                text = stringResource(Res.string.autoHideMods),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    WarningCallout()
                }
            }
        }
    }
}

@Composable
private fun StatusText(state: MainState.State) {
    val (message, color) = when {
        state.selectedFolderPath == null -> stringResource(Res.string.gamePathNull) to MaterialTheme.colorScheme.error
        state.addonEnabledList.isNullOrEmpty() -> stringResource(Res.string.gameAddonsNotSelect) to MaterialTheme.colorScheme.error
        else -> stringResource(Res.string.gameDoneFAQ) to MaterialTheme.colorScheme.onSurface
    }

    Text(
        text = message,
        style = MaterialTheme.typography.titleMedium,
        color = color
    )
}

@Composable
private fun WarningCallout() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Text(
                text = stringResource(Res.string.addonsWarningEnable),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
