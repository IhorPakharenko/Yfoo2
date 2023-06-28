@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class,
    ExperimentalMaterial3Api::class
)

package com.isao.yfoo2.presentation.liked.composable

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isao.yfoo2.R
import com.isao.yfoo2.core.extensions.collectWithLifecycle
import com.isao.yfoo2.core.theme.Yfoo2Theme
import com.isao.yfoo2.core.utils.DevicePreviews
import com.isao.yfoo2.domain.model.ImageSource
import com.isao.yfoo2.presentation.liked.LikedEvent
import com.isao.yfoo2.presentation.liked.LikedIntent
import com.isao.yfoo2.presentation.liked.LikedUiState
import com.isao.yfoo2.presentation.liked.LikedViewModel
import com.isao.yfoo2.presentation.model.LikedImageDisplayable
import kotlinx.coroutines.flow.Flow

@Composable
fun LikedRoute(
    viewModel: LikedViewModel = hiltViewModel()
) {
    HandleEvents(viewModel.event)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LikedScreen(uiState = uiState, onIntent = viewModel::acceptIntent)
}

@Composable
private fun HandleEvents(events: Flow<LikedEvent>) {
    val uriHandler = LocalUriHandler.current

    events.collectWithLifecycle {
        when (it) {
            is LikedEvent.OpenWebBrowser -> {
                uriHandler.openUri(it.uri)
            }
        }
    }
}

private enum class ScreenContent {
    ITEMS, NO_ITEMS, LOADING, ERROR
}

private val LikedUiState.screenContent
    get() = when {
        isLoading -> ScreenContent.LOADING
        isError -> ScreenContent.ERROR
        items.isEmpty() -> ScreenContent.NO_ITEMS
        else -> ScreenContent.ITEMS
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedScreen(
    uiState: LikedUiState,
    onIntent: (LikedIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
                scrollBehavior = scrollBehavior
            )
        },
        // Let the content take up all available space.
        // Material3 components handle the insets themselves
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { padding ->
        Crossfade(uiState.screenContent) { screenContent ->
            when (screenContent) {
                ScreenContent.LOADING -> LoadingPlaceholder()
                ScreenContent.ERROR -> ErrorPlaceholder()
                ScreenContent.NO_ITEMS -> NoItemsPlaceholder()
                ScreenContent.ITEMS -> ItemsAvailableContent(
                    uiState = uiState,
                    onIntent = onIntent,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                )
            }
        }
    }
}

// Restarting the image request after regaining network connectivity is an open issue
// in coil since 2019: https://github.com/coil-kt/coil/issues/132
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsAvailableContent(
    uiState: LikedUiState,
    onIntent: (LikedIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItem by rememberSaveable { mutableStateOf<LikedImageDisplayable?>(null) }

    val itemSize = 100.dp

    LazyVerticalGrid(
        columns = GridCells.Adaptive(itemSize),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        item(span = {
            GridItemSpan(this.maxLineSpan)
        }) {
            LikedGridSettings(
                sortAscending = uiState.shouldSortAscending,
                setSortAscending = { onIntent(LikedIntent.SetSorting(it)) }
            )
        }
        items(
            uiState.items,
            key = { it.id }
        ) { item ->
            Box(
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .animateItemPlacement()
            ) {
                val isSelected by remember { derivedStateOf { selectedItem == item } }
                val sizeFraction by animateFloatAsState(if (isSelected) 0.85f else 1f)
                LikedItem(
                    item = item,
                    width = itemSize,
                    height = itemSize,
                    onClick = { onIntent(LikedIntent.ImageClicked(item)) },
                    onLongClick = { selectedItem = item },
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center)
                        .graphicsLayer {
                            scaleX = sizeFraction
                            scaleY = sizeFraction
                        }
                )
                ImageActionsPopup(
                    expanded = isSelected,
                    item = item,
                    onDismissRequest = { selectedItem = null },
                    onSourceClick = { onIntent(LikedIntent.ViewImageSourceClicked(item)) },
                    onDeleteClick = { onIntent(LikedIntent.DeleteImageClicked(item)) },
                )
            }
        }
    }
}

@Composable
private fun ImageActionsPopup(
    expanded: Boolean,
    item: LikedImageDisplayable,
    onDismissRequest: () -> Unit,
    onSourceClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
    ) {
        ClickableText(
            text = buildAnnotatedString {
                val websiteName = item.source.websiteName
                val fullString =
                    stringResource(R.string.image_by, item.source.websiteName)
                val websiteStart = fullString.indexOf(websiteName)
                val websiteEnd = websiteStart + websiteName.length

                append(fullString)

                addStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    start = 0,
                    end = fullString.length
                )
                addStyle(
                    style = SpanStyle(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    start = websiteStart,
                    end = websiteEnd
                )
            },
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            onClick = {
                onSourceClick()
                onDismissRequest()
            }
        )
        Spacer(Modifier.height(24.dp))
        DropdownMenuItem(
            text = {
                Text(text = stringResource(R.string.delete))
            },
            onClick = {
                onDeleteClick()
                onDismissRequest()
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        )
    }
}

@Composable
private fun NoItemsPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.nothing_is_there_yet),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.errorContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.something_went_wrong),
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            Modifier
                .padding(16.dp)
                .requiredSize(40.dp)
        )
    }
}

@DevicePreviews
@Composable
fun LikedScreenPreview() {
    Yfoo2Theme {
        LikedScreen(
            uiState = LikedUiState(
                items = List(50) {
                    LikedImageDisplayable(
                        id = it.toString(),
                        imageUrl = "",
                        source = ImageSource.THIS_WAIFU_DOES_NOT_EXIST
                    )
                }
            ),
            onIntent = {}
        )
    }
}

@DevicePreviews
@Composable
fun LikedScreenLoadingPreview() {
    Yfoo2Theme {
        LikedScreen(
            uiState = LikedUiState(
                isLoading = true
            ),
            onIntent = {}
        )
    }
}

@DevicePreviews
@Composable
fun LikedScreenNoItemsPreview() {
    Yfoo2Theme {
        LikedScreen(
            uiState = LikedUiState(),
            onIntent = {}
        )
    }
}

@DevicePreviews
@Composable
fun LikedScreenErrorPreview() {
    Yfoo2Theme {
        LikedScreen(
            uiState = LikedUiState(
                isError = true,
            ),
            onIntent = {}
        )
    }
}