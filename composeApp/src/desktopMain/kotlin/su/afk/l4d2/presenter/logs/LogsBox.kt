package su.afk.l4d2.presenter.logs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

import su.afk.l4d2.data.LogSystem

@Composable
fun LogsBox() {

    // Отслеживаем состояние логов через StateFlow
    val logs = LogSystem.logsFlow.collectAsState().value

    // Фильтрация логов: check = false и priority = 1, 2, 3
    val filteredLogs = remember(logs) {
        logs.filter { !it.check && it.priority in 1..3 }
    }

    // Если есть логи для отображения, показываем их
    if (filteredLogs.isNotEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight() // Высота подстраивается под содержимое
                .background(
                    MaterialTheme.colors.onBackground.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(4.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 200.dp), // Максимальная высота для списка логов
                verticalArrangement = Arrangement.Top
            ) {
                items(filteredLogs) { log ->
                    LogRow(
                        log = log,
                        onDismiss = {
                            LogSystem.markLogAsChecked(log)
                        }
                    )
                }
            }
        }
    }
    // Если логов нет, ничего не отображаем, и область не блокирует клики
}

@Composable
fun LogRow(
    log: LogSystem.ErrorLog,
    onDismiss: () -> Unit
) {
    // Автоматическое закрытие лога через 5 секунд
    LaunchedEffect(log.id) {
        delay(8000)
        onDismiss()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Текст лога
        Text(
            text = log.message,
            color = Color.White,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(4.dp))

        // Иконка "крестик"
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Закрыть лог",
                tint = when (log.priority) {
                    1 -> Color.Red
                    2 -> Color.Yellow
                    3 -> Color.Green
                    else -> Color.White
                },
                modifier = Modifier.size(20.dp)
            )
        }
    }
}