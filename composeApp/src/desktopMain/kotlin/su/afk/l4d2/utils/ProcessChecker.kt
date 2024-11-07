package su.afk.l4d2.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import su.afk.l4d2.MainState
import su.afk.l4d2.MainViewModel
import java.io.BufferedReader
import java.io.InputStreamReader

class ProcessChecker {
    companion object {
        fun checkProcess(viewModel: MainViewModel) {
            CoroutineScope(Dispatchers.Default).launch {
                var processRunning = false
                while (true) {
                    val isRunning = isProcessRunning("left4dead2.exe")

                    if (isRunning && !processRunning) {
                        viewModel.handlerEvents(MainState.Event.ProcessStarted)
                        processRunning = true
                    } else if (!isRunning && processRunning) {
                        viewModel.handlerEvents(MainState.Event.ProcessStopped)
                        processRunning = false
                    }

                    delay(2000) // Задержка перед следующей проверкой (2 секунды)
                }
            }
        }
    }
}


fun isProcessRunning(processName: String): Boolean {
    try {
        val process = ProcessBuilder("tasklist").start()
        BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line!!.contains(processName, ignoreCase = true)) {
                    return true
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}