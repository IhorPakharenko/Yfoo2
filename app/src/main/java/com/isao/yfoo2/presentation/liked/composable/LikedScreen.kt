package com.isao.yfoo2.presentation.liked.composable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isao.yfoo2.R
import com.isao.yfoo2.core.extensions.collectWithLifecycle
import com.isao.yfoo2.core.theme.Yfoo2Theme
import com.isao.yfoo2.core.utils.DevicePreviews
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
    val itemSize = 100.dp

    LazyVerticalGrid(
        columns = GridCells.Adaptive(itemSize),
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        content = {
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
                LikedItem(
                    item = item,
                    width = itemSize,
                    height = itemSize,
                    onClick = { onIntent(LikedIntent.ImageClicked(item)) },
                    onLongClick = { onIntent(LikedIntent.ImageLongClicked(item)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .animateItemPlacement()
                )
            }
        },
    )
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
                        sourceUrl = ""
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

//TODO remove the commented code below. It works poorly,
// but I spent too much time on it to remove it straight away.

//        val context = LocalContext.current
//        var cachedImages by remember { mutableStateOf(emptyList<LikedImageDisplayable>()) }
//        LaunchedEffect(uiState) {
//            uiState.items.forEach { item ->
//                Coil.imageLoader(context).execute(
//                    ImageRequest.Builder(context)
//                        .allowHardware(false)
//                        .data(item.imageUrl)
//                        .transformations(BorderCropTransformation())
//                        .build()
//                )
////                cachedImages = cachedImages + item
//            }
//            cachedImages = cachedImages + uiState.items
//        }


//        val gridState = rememberLazyGridState()

//        LaunchedEffect(gridState, uiState.pageSize) {
//            snapshotFlow { gridState.layoutInfo }
//                .map { info ->
//                    val lastVisibleItemIndex =
//                        info.visibleItemsInfo.lastOrNull()?.index ?: return@map false
//                    val lastPossibleIndex = info.totalItemsCount - 1
//                    lastPossibleIndex - lastVisibleItemIndex < uiState.pageSize
//                }
//                .distinctUntilChanged()
//                .filter { it }
//                .collect {
//                    onIntent(LikedIntent.GetImages)
//                }
//        }