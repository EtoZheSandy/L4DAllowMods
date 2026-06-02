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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinproject.composeapp.generated.resources.Res
import kotlinproject.composeapp.generated.resources.addonsNoShow
import kotlinproject.composeapp.generated.resources.addonsNotFoundEmpty
import kotlinproject.composeapp.generated.resources.addonsShow
import kotlinproject.composeapp.generated.resources.addonsSearchEmpty
import kotlinproject.composeapp.generated.resources.disable
import kotlinproject.composeapp.generated.resources.enable
import kotlinproject.composeapp.generated.resources.enabled
import kotlinproject.composeapp.generated.resources.gamePathNull
import kotlinproject.composeapp.generated.resources.myAddons
import kotlinproject.composeapp.generated.resources.noDescription
import kotlinproject.composeapp.generated.resources.searchAddons
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
    val searchQuery = remember { mutableStateOf("") }
    val isFilterAsc = remember { mutableStateOf(true) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(Res.string.myAddons),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (state.selectedFolderPath == null) {
            EmptyStateCard(
                message = stringResource(Res.string.gamePathNull),
                isError = true
            )
            return@Column
        }

        AddonToolbar(
            showListAddons = showListAddons,
            loading = state.loadingAddonInfo,
            searchQuery = searchQuery.value,
            isFilterAsc = isFilterAsc.value,
            onToggleList = {
                if (!showListAddons) {
                    onEvent(MainState.Event.ReadVPKFiles)
                }
                onEvent(MainState.Event.ShowListAddons)
            },
            onSort = {
                isFilterAsc.value = !isFilterAsc.value
                onEvent(MainState.Event.SortAddons(isFilterAsc.value))
            },
            onSearchChange = { searchQuery.value = it },
            onClearSearch = { searchQuery.value = "" }
        )

        if (showListAddons) {
            val sortedAddonList = remember(state.sortedAddonList, state.addonInfoList) {
                state.sortedAddonList ?: state.addonInfoList ?: emptyList()
            }
            val filteredAddonList = remember(sortedAddonList, searchQuery.value) {
                sortedAddonList.filter { addon ->
                    addon.matchesSearchQuery(searchQuery.value)
                }
            }

            AddonListScreen(
                addonInfoList = filteredAddonList,
                state = state,
                emptyListMessage = stringResource(
                    if (searchQuery.value.isBlank()) {
                        Res.string.addonsNotFoundEmpty
                    } else {
                        Res.string.addonsSearchEmpty
                    }
                ),
                onEvent = onEvent
            )
        }
    }
}

@Composable
private fun AddonToolbar(
    showListAddons: Boolean,
    loading: Boolean,
    searchQuery: String,
    isFilterAsc: Boolean,
    onToggleList: () -> Unit,
    onSort: () -> Unit,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onToggleList,
                enabled = !loading
            ) {
                Text(
                    if (showListAddons) {
                        stringResource(Res.string.addonsNoShow)
                    } else {
                        stringResource(Res.string.addonsShow)
                    }
                )
            }

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.size(26.dp))
            }

            if (showListAddons) {
                IconButton(
                    onClick = onSort,
                    enabled = !loading
                ) {
                    Icon(
                        painter = org.jetbrains.compose.resources.painterResource(Res.drawable.sort),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .graphicsLayer { rotationX = if (isFilterAsc) 0f else 180f },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text(stringResource(Res.string.searchAddons)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun AddonListScreen(
    addonInfoList: List<AddonInfo>,
    state: MainState.State,
    emptyListMessage: String,
    onEvent: (MainState.Event) -> Unit
) {
    val lazyListState = rememberLazyListState()
    val selectedAddonFilenames = remember(state.addonEnabledList) {
        state.addonEnabledList.orEmpty().map { it.filename }.toSet()
    }

    if (addonInfoList.isEmpty() && !state.loadingAddonInfo) {
        EmptyStateCard(message = emptyListMessage, isError = true)
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            state = lazyListState
        ) {
            items(
                items = addonInfoList,
                key = { addon -> addon.filename }
            ) { addon ->
                AddonCard(
                    addon = addon,
                    selected = selectedAddonFilenames.contains(addon.filename),
                    onToggle = { onEvent(MainState.Event.ClickMods(addon)) }
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            adapter = rememberScrollbarAdapter(lazyListState)
        )
    }
}

@Composable
private fun AddonCard(
    addon: AddonInfo,
    selected: Boolean,
    onToggle: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Column(
                modifier = Modifier.width(190.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AddonImage(addon.imagePath)

                FilledTonalButton(
                    onClick = onToggle,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = if (selected) {
                            MaterialTheme.colorScheme.surfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        contentColor = if (selected) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onPrimary
                        }
                    )
                ) {
                    Text(
                        if (selected) {
                            stringResource(Res.string.disable)
                        } else {
                            stringResource(Res.string.enable)
                        }
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = addon.title,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (selected) {
                        AssistChip(
                            onClick = {},
                            label = { Text(stringResource(Res.string.enabled)) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }

                    IconButton(
                        onClick = {
                            openGitHubLink("https://steamcommunity.com/sharedfiles/filedetails/?id=${addon.filename}")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))

                Text(
                    text = addon.description ?: stringResource(Res.string.noDescription),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun AddonImage(imagePath: String?) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(8.dp)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (imagePath != null) {
            val imageBitmap = produceState<ImageBitmap?>(initialValue = null, imagePath) {
                value = withContext(Dispatchers.IO) {
                    loadImageBitmap(
                        file = File(imagePath),
                        maxWidth = 380,
                        maxHeight = 214
                    )
                }
            }
            imageBitmap.value?.let { bitmap ->
                Image(
                    bitmap = bitmap,
                    contentDescription = "Addon Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "VPK",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    message: String,
    isError: Boolean
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(18.dp),
            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

private fun AddonInfo.matchesSearchQuery(query: String): Boolean {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isEmpty()) return true

    return title.contains(normalizedQuery, ignoreCase = true) ||
        description?.contains(normalizedQuery, ignoreCase = true) == true ||
        filename.contains(normalizedQuery, ignoreCase = true)
}
