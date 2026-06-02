package su.afk.l4d2.utils

import kotlinx.coroutines.delay
import su.afk.l4d2.MainState
import su.afk.l4d2.MainViewModel

class ProcessChecker {
    companion object {
        suspend fun checkProcess(viewModel: MainViewModel) {
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

                delay(2000)
            }
        }
    }
}


fun isProcessRunning(processName: String): Boolean {
    val targetName = processName.lowercase()
    val processes = ProcessHandle.allProcesses().iterator()

    while (processes.hasNext()) {
        val process = processes.next()
        val info = process.info()
        val command = info.command().orElse("").substringAfterLast('/').substringAfterLast('\\')
        val commandLine = info.commandLine().orElse("")

        if (command.equals(processName, ignoreCase = true) ||
            commandLine.lowercase().contains(targetName)
        ) {
            return true
        }
    }

    return false
}
