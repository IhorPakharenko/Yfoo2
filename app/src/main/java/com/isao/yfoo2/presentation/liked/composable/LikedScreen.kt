package com.isao.yfoo2.presentation.liked.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isao.yfoo2.R
import com.isao.yfoo2.core.utils.collectWithLifecycle
import com.isao.yfoo2.presentation.liked.LikedEvent
import com.isao.yfoo2.presentation.liked.LikedIntent
import com.isao.yfoo2.presentation.liked.LikedUiState
import com.isao.yfoo2.presentation.liked.LikedViewModel
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
        }
    ) { padding ->
        if (uiState.isLoading) {
            LoadingPlaceholder()
            return@Scaffold
        }

        if (uiState.isError) {
            ErrorPlaceholder()
            return@Scaffold
        }

        if (uiState.items.isEmpty()) {
            NoItemsPlaceholder()
            return@Scaffold
        }

        ItemsAvailableContent(
            uiState = uiState,
            onIntent = onIntent,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemsAvailableContent(
    uiState: LikedUiState,
    onIntent: (LikedIntent) -> Unit,

    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(100.dp),
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
                key = {
                    it.id
                }
            ) { item ->
                LikedItem(
                    item = item,
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
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.nothing_is_there_yet),
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.something_went_wrong),
            color = Color.Red,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun LoadingPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            Modifier
                .padding(16.dp)
                .requiredSize(40.dp)
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