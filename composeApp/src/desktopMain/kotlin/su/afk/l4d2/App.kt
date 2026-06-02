package su.afk.l4d2

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.AppTheme
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.faq
import kotlinproject.composeapp.generated.resources.logs
import kotlinproject.composeapp.generated.resources.main
import kotlinproject.composeapp.generated.resources.myAddons
import kotlinproject.composeapp.generated.resources.setting
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import su.afk.l4d2.main.MainState
import su.afk.l4d2.main.MainViewModel
import su.afk.l4d2.presenter.addonList.AddonList
import su.afk.l4d2.presenter.faq.FAQScreen
import su.afk.l4d2.presenter.logs.LogsBox
import su.afk.l4d2.presenter.logs.LogsScreen
import su.afk.l4d2.presenter.main.MainScreen
import su.afk.l4d2.presenter.setting.SettingsScreen
import su.afk.l4d2.utils.ProcessChecker
import su.afk.l4d2.utils.openGitHubLink
import kotlin.reflect.KClass

@Composable
@Preview
fun App(onCloseRequest: () -> Unit) {
    // Состояние для хранения текущего экрана
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }

    val viewModel = viewModel<MainViewModel>(
        factory = MainViewModelFactory
    )

    LaunchedEffect(viewModel) {
        ProcessChecker.checkProcess(viewModel)
    }

    AppTheme {
        // Основной контейнер, разделяющий экран на две части
        Row(modifier = Modifier.fillMaxSize()) {
            // Левая колонка с кнопками навигации
            NavigationColumn(onNavigate = { screen ->
                currentScreen = screen
            })

            Divider(modifier = Modifier.fillMaxHeight().width(1.dp))

            // Правая область контента
            ContentArea(
                currentScreen = currentScreen,
                modifier = Modifier.weight(1f),
                viewModel = viewModel,
                onCloseRequest = onCloseRequest
            )
        }
    }

}


@Composable
fun NavigationColumn(onNavigate: (Screen) -> Unit) {
    Column(
        modifier = Modifier
            .width(165.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colors.onBackground)
            .padding(12.dp)
    ) {
        Text(
            "L4DAllowMods",
            style = MaterialTheme.typography.h6,
            color = MaterialTheme.colors.secondary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onNavigate(Screen.Main) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.main))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onNavigate(Screen.AddonsList) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.myAddons))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onNavigate(Screen.FAQ) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.faq))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onNavigate(Screen.Settings) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.setting))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onNavigate(Screen.Logs) }, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(Res.string.logs))
        }

        Spacer(modifier = Modifier.weight(1f)) // Занимает всё свободное пространство

        Button(
            onClick = {
                openGitHubLink("https://github.com/EtoZheSandy/L4DAllowMods")
            },
            modifier = Modifier.align(Alignment.Start),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.LightGray
            )
        ) {
            Text("Github")
        }
    }
}

@Composable
fun ContentArea(
    currentScreen: Screen,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    onCloseRequest: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val onEvent = remember(viewModel) {
        { event: MainState.Event -> viewModel.handlerEvents(event) }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(16.dp)
    ) {
        // Основное содержимое экранов
        Crossfade(targetState = currentScreen) { screen ->
            when (screen) {
                is Screen.Main -> MainScreen(state = state, onEvent = onEvent)
                is Screen.AddonsList -> AddonList(state = state, onEvent = onEvent)
                is Screen.FAQ -> FAQScreen()
                is Screen.Settings -> SettingsScreen(state = state, onEvent = onEvent)
                is Screen.Logs -> LogsScreen()
            }
        }

        // Box для логов, который располагается поверх основного контента внизу
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(4.dp)
        ) {
            LogsBox()
        }

        Box(
            modifier = Modifier.align(Alignment.TopEnd)
                .offset(x = 12.dp, y = (-12).dp) // Смещение на 10 пикселей вправо и вверх
        ) {
            IconButton(
                onClick = onCloseRequest
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Close,
                    contentDescription = "Close"
                )
            }
        }
    }
}

// Перечисление экранов
sealed class Screen {
    object Main : Screen()
    object AddonsList : Screen()
    object FAQ : Screen()
    object Settings : Screen()
    object Logs : Screen()
}

private object MainViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: KClass<T>, extras: CreationExtras): T {
        if (modelClass == MainViewModel::class) {
            return MainViewModel() as T
        }

        error("Unknown ViewModel class: ${modelClass.qualifiedName}")
    }
}
