package su.afk.l4d2.presenter.logs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import su.afk.l4d2.data.LogSystem

@Composable
fun LogsBox() {
    val logs = LogSystem.logsFlow.collectAsStateWithLifecycle().value
    val filteredLogs = remember(logs) {
        logs.filter { !it.check && it.priority in 1..3 }
    }

    if (filteredLogs.isNotEmpty()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 220.dp),
            color = MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.96f),
            contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            shape = RoundedCornerShape(8.dp),
            tonalElevation = 8.dp,
            shadowElevation = 8.dp
        ) {
            LazyColumn(
                modifier = Modifier.padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(filteredLogs) { log ->
                    LogRow(
                        log = log,
                        onDismiss = { LogSystem.markLogAsChecked(log) }
                    )
                }
            }
        }
    }
}

@Composable
fun LogRow(
    log: LogSystem.ErrorLog,
    onDismiss: () -> Unit
) {
    LaunchedEffect(log.id) {
        delay(8000)
        onDismiss()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = log.icon(),
            contentDescription = null,
            tint = log.tint(),
            modifier = Modifier.size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = log.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
        }
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close log",
                tint = MaterialTheme.colorScheme.inverseOnSurface,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

private fun LogSystem.ErrorLog.icon(): ImageVector =
    when (priority) {
        1 -> Icons.Filled.Error
        2 -> Icons.Filled.Info
        else -> Icons.Filled.CheckCircle
    }

@Composable
private fun LogSystem.ErrorLog.tint() =
    when (priority) {
        1 -> MaterialTheme.colorScheme.error
        2 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }
