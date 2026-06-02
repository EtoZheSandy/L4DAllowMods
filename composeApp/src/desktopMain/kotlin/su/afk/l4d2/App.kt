package su.afk.l4d2

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.faq
import kotlinproject.composeapp.generated.resources.logs
import kotlinproject.composeapp.generated.resources.main
import kotlinproject.composeapp.generated.resources.myAddons
import kotlinproject.composeapp.generated.resources.setting
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import su.afk.l4d2.domain.model.UpdateCheckState
import su.afk.l4d2.main.MainState
import su.afk.l4d2.main.MainViewModel
import su.afk.l4d2.presenter.addonList.AddonList
import su.afk.l4d2.presenter.faq.FAQScreen
import su.afk.l4d2.presenter.logs.LogsBox
import su.afk.l4d2.presenter.logs.LogsScreen
import su.afk.l4d2.presenter.main.MainScreen
import su.afk.l4d2.presenter.setting.SettingsScreen
import su.afk.l4d2.presenter.view.theme.AppTheme
import su.afk.l4d2.utils.ProcessChecker
import su.afk.l4d2.utils.openGitHubLink
import kotlin.reflect.KClass

@Composable
@Preview
fun App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Main) }

    val viewModel = viewModel<MainViewModel>(
        factory = MainViewModelFactory
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        ProcessChecker.checkProcess(viewModel)
    }

    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationColumn(
                    currentScreen = currentScreen,
                    hasUpdate = state.updateCheckState is UpdateCheckState.UpdateAvailable,
                    onNavigate = { screen -> currentScreen = screen }
                )

                HorizontalDivider(
                    modifier = Modifier.fillMaxHeight().width(1.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.55f)
                )

                ContentArea(
                    currentScreen = currentScreen,
                    modifier = Modifier.weight(1f),
                    state = state,
                    onEvent = viewModel::handlerEvents
                )
            }
        }
    }
}

@Composable
fun NavigationColumn(
    currentScreen: Screen,
    hasUpdate: Boolean,
    onNavigate: (Screen) -> Unit
) {
    val items = listOf(
        NavigationItem(Screen.Main, Res.string.main, Icons.Filled.Home),
        NavigationItem(Screen.AddonsList, Res.string.myAddons, Icons.Filled.Extension),
        NavigationItem(Screen.Settings, Res.string.setting, Icons.Filled.Settings),
        NavigationItem(Screen.FAQ, Res.string.faq, Icons.AutoMirrored.Filled.Help),
        NavigationItem(Screen.Logs, Res.string.logs, Icons.AutoMirrored.Filled.Article)
    )

    Surface(
        modifier = Modifier
            .width(168.dp)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "L4DAllowMods",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Workshop utility",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { item ->
                NavigationButton(
                    item = item,
                    selected = currentScreen::class == item.screen::class,
                    showBadge = hasUpdate && item.screen is Screen.Settings,
                    onClick = { onNavigate(item.screen) }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                onClick = { openGitHubLink("https://github.com/EtoZheSandy/L4DAllowMods") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("GitHub")
            }
        }
    }
}

@Composable
private fun NavigationButton(
    item: NavigationItem,
    selected: Boolean,
    showBadge: Boolean,
    onClick: () -> Unit
) {
    val containerColor = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (selected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(item.label),
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
        if (showBadge) {
            Spacer(modifier = Modifier.weight(1f))
            Badge(
                modifier = Modifier.size(10.dp),
                containerColor = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun ContentArea(
    currentScreen: Screen,
    modifier: Modifier = Modifier,
    state: MainState.State,
    onEvent: (MainState.Event) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Crossfade(targetState = currentScreen) { screen ->
            when (screen) {
                is Screen.Main -> MainScreen(state = state, onEvent = onEvent)
                is Screen.AddonsList -> AddonList(state = state, onEvent = onEvent)
                is Screen.FAQ -> FAQScreen()
                is Screen.Settings -> SettingsScreen(state = state, onEvent = onEvent)
                is Screen.Logs -> LogsScreen()
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(4.dp)
        ) {
            LogsBox()
        }
    }
}

private data class NavigationItem(
    val screen: Screen,
    val label: StringResource,
    val icon: ImageVector
)

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
