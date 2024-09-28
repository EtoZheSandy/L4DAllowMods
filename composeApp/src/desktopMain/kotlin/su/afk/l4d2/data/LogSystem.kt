package su.afk.l4d2.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicInteger

object LogSystem {

    data class ErrorLog(
        val id: Int,
        val priority: Int,
        val message: String,
        var check: Boolean = false
    )

    private val nextLogId = AtomicInteger(1) // Потокобезопасный счетчик ID логов


    // StateFlow для отслеживания состояния логов
    private val _logsFlow = MutableStateFlow<List<ErrorLog>>(emptyList())
    val logsFlow = _logsFlow.asStateFlow() // Предоставление публичного доступа как Read-Only StateFlow


    /**
     * Добавить лог в приложение, приоритет 1-3 отображается на экране
     * Используйте synchronized для потокобезопасного добавления лога.
     */
    fun addLog(priority: Int, message: String) {
        val errorLog = ErrorLog(
            id = nextLogId.getAndIncrement(),
            priority = priority,
            message = message
        )
        sendLog(errorLog)
    }
    /**
     * Добавление лога в систему с синхронизацией для обеспечения потокобезопасности.
     */
    private fun sendLog(errorLog: ErrorLog) {
        _logsFlow.update { currentLogs ->
            currentLogs + errorLog
        }
    }

    /**
     * Метод для установки состояния логов.
     */
    fun markLogAsChecked(log: ErrorLog) {
        _logsFlow.update { currentLogs ->
            currentLogs.map { if (it.id == log.id) it.copy(check = true) else it }
        }
    }
}