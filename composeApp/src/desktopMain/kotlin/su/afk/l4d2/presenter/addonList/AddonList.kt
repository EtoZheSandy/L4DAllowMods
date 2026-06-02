package su.afk.l4d2.presenter.addonList

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.addonsNoShow
import kotlinproject.composeapp.generated.resources.addonsNotFoundEmpty
import kotlinproject.composeapp.generated.resources.addonsShow
import kotlinproject.composeapp.generated.resources.disable
import kotlinproject.composeapp.generated.resources.enable
import kotlinproject.composeapp.generated.resources.gamePathNull
import kotlinproject.composeapp.generated.resources.noDescription
import kotlinproject.composeapp.generated.resources.sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import loadImageBitmap
import org.jetbrains.compose.resources.stringResource
import su.afk.l4d2.domain.model.AddonInfo
import su.afk.l4d2.main.MainState
import su.afk.l4d2.utils.openGitHubLink
import java.io.File


@Composable
fun AddonList(
    state: MainState.State,
    onEvent: (MainState.Event) -> Unit
) {
    val showListAddons = state.showListAddons

    //todo добавить поиск по модам аля окно поиска для ввода
    val isFilterAsc =
        remember { mutableStateOf(true) } // Состояние фильтрации (true - включенные сначала)

    Column {
        if (state.selectedFolderPath == null) {
            Text(
                stringResource(Res.string.gamePathNull),
                color = MaterialTheme.colors.error
            )
        } else {
            Row(
                modifier = Modifier
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically // Выравнивание по центру по вертикали
            ) {
                Button(
                    onClick = {
                        if (!showListAddons) {
                            onEvent(MainState.Event.ReadVPKFiles)
                        }
                        onEvent(MainState.Event.ShowListAddons)
                    },
                    enabled = !state.loadingAddonInfo
                )
                {
                    if (showListAddons) {
                        Text(stringResource(Res.string.addonsNoShow))
                    } else {
                        Text(stringResource(Res.string.addonsShow))
                    }
                }

                if (state.loadingAddonInfo) {
                    Spacer(modifier = Modifier.width(12.dp)) // Отступ между Button и CircularProgressIndicator
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp), // Уменьшаем размер индикатора
                        color = MaterialTheme.colors.primary // Можно настроить цвет по желанию
                    )
                }

                if (showListAddons) {
                    if (!state.loadingAddonInfo) {
                        // Кнопка для фильтрации списка
                        IconButton(
                            onClick = {
                                isFilterAsc.value = !isFilterAsc.value // Меняем порядок фильтрации
                                onEvent(MainState.Event.SortAddons(isFilterAsc.value))
                            },
                            modifier = Modifier
                                .padding(start = 12.dp)
                                .clip(CircleShape) // Делает кнопку круглой
                                .background(MaterialTheme.colors.primary.copy(alpha = 0.8f)) // Задает фоновый цвет с прозрачностью
                        ) {
                            // Меняем иконку в зависимости от состояния фильтрации
                            if (isFilterAsc.value) {
                                Icon(
                                    painter = org.jetbrains.compose.resources.painterResource(Res.drawable.sort),
                                    contentDescription = null,
                                    modifier = Modifier.size(32.dp),
                                )
                            } else {
                                Icon(
                                    painter = org.jetbrains.compose.resources.painterResource(Res.drawable.sort),
                                    contentDescription = null,
                                    modifier = if (!isFilterAsc.value) Modifier.size(32.dp)
                                        .graphicsLayer {
                                            this.rotationX =
                                                180f // Переворачиваем иконку по горизонтали
                                        } else Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (showListAddons) {
                // Используем отсортированный список из ViewModel
                val sortedAddonList = remember(state.sortedAddonList, state.addonInfoList) {
                    state.sortedAddonList ?: state.addonInfoList ?: emptyList()
                }

                AddonListScreen(
                    addonInfoList = sortedAddonList,
                    state = state,
                    onEvent = onEvent
                )
            }
        }
    }
}


@Composable
fun AddonListScreen(
    addonInfoList: List<AddonInfo>,
    state: MainState.State,
    onEvent: (MainState.Event) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val selectedAddonFilenames = remember(state.addonEnabledList) {
        state.addonEnabledList.orEmpty().map { it.filename }.toSet()
    }

    if (addonInfoList.isEmpty()) {
        if (!state.loadingAddonInfo) {
            Text(
                stringResource(Res.string.addonsNotFoundEmpty),
                color = MaterialTheme.colors.error
            )
        }
    }

    // Используем LazyColumn с автоматической прокруткой и настройками
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp), // Добавляем отступ справа для полосы прокрутки
            verticalArrangement = Arrangement.spacedBy(8.dp), // Отступы между элементами списка
            state = lazyListState
        ) {
            items(addonInfoList) { addon ->
                Row(
                    modifier = Modifier.padding(6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Колонка с изображением
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    ) {
                        if (addon.imagePath != null) {
                            val imageBitmap = produceState<ImageBitmap?>(initialValue = null, addon.imagePath) {
                                value = withContext(Dispatchers.IO) {
                                    loadImageBitmap(File(addon.imagePath))
                                }
                            }
                            imageBitmap.value?.let { bitmap ->
                                Image(
                                    bitmap = bitmap,
                                    contentDescription = "Addon Image",
                                    modifier = Modifier
                                        .aspectRatio(4f / 2f) // Соотношение сторон 2:1
                                        .fillMaxWidth() // Занимает всю доступную ширину
                                )
                            }
                        }
                        Row(
//                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    onEvent(MainState.Event.ClickMods(addon))
                                },
                                modifier = Modifier.weight(1f) // Устанавливаем вес, чтобы Button занимал пропорциональное пространство
                                    .padding(start = 30.dp, end = 10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor =
                                    if (selectedAddonFilenames.contains(addon.filename)) Color.LightGray
                                    else Color.Cyan
                                )
                            ) {
                                if (selectedAddonFilenames.contains(addon.filename)) {
                                    Text(stringResource(Res.string.disable))
                                } else {
                                    Text(stringResource(Res.string.enable))
                                }
                            }
                            IconButton(
                                onClick = {
                                    openGitHubLink("https://steamcommunity.com/sharedfiles/filedetails/?id=${addon.filename}")
                                },
                                modifier = Modifier.size(25.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                )
                            }
                        }
                    }

                    // Колонка с названием и описанием
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .padding(start = 6.dp)
                    ) {
                        Text(
                            text = "${addon.title}",
                            style = MaterialTheme.typography.h6,
                            color = MaterialTheme.colors.onSurface
                        )
                        Text(
                            text = addon.description ?: stringResource(Res.string.noDescription),
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
                // Разделитель после каждого элемента
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .width(1.dp)
                        .padding(horizontal = 16.dp) // Добавим отступы для эстетики
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(lazyListState)
        )
    }
}
