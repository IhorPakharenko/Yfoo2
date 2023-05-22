@file:OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)

package com.isao.yfoo2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.isao.yfoo2.ui.Direction
import com.isao.yfoo2.ui.DismissibleState
import com.isao.yfoo2.ui.Screen
import com.isao.yfoo2.ui.YfooNavGraph
import com.isao.yfoo2.ui.dismissible
import com.isao.yfoo2.ui.rememberDismissibleState
import com.isao.yfoo2.ui.scale
import com.isao.yfoo2.ui.theme.Yfoo2Theme
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.random.Random

enum class BottomNavigationScreen(
    val route: String,
    @StringRes val nameRes: Int,
    val icon: ImageVector
) {
    Feed(Screen.Feed.route, R.string.feed, Icons.Filled.Explore),
    Liked(Screen.Liked.route, R.string.liked, Icons.Filled.Favorite),
}

@ExperimentalMaterial3Api
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Yfoo2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberAnimatedNavController()
                    val bottomNavigationScreens = remember {
                        listOf(
                            BottomNavigationScreen.Feed,
                            BottomNavigationScreen.Liked
                        )
                    }
                    Scaffold(bottomBar = {
                        NavigationBar {
                            val navBackStackEntry by navController.currentBackStackEntryAsState()
                            val currentDestination = navBackStackEntry?.destination

                            bottomNavigationScreens.forEach { screen ->
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = null
                                        )
                                    },
                                    label = { Text(stringResource(screen.nameRes)) },
                                    selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            // Pop up to the start destination of the graph to
                                            // avoid building up a large stack of destinations
                                            // on the back stack as users select items
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            // Avoid multiple copies of the same destination when
                                            // reselecting the same item
                                            launchSingleTop = true
                                            // Restore state when reselecting a previously selected item
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }) { padding ->
                        YfooNavGraph(
                            navController = navController,
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(padding),
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun FeedScreen(modifier: Modifier = Modifier) {
    var topAppBarBounds by remember { mutableStateOf<Rect?>(null) }
    var topCardBounds by remember { mutableStateOf<Rect?>(null) }
    val showTopAppBarDivider by remember {
        derivedStateOf {
            topCardBounds?.overlaps(topAppBarBounds ?: return@derivedStateOf false) == true
        }
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(stringResource(R.string.app_name))
            },
            modifier = Modifier.onGloballyPositioned {
                topAppBarBounds = it.boundsInRoot()
            },
            colors = if (showTopAppBarDivider) {
                TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(10.dp)
                )
            } else {
                TopAppBarDefaults.centerAlignedTopAppBarColors()
            }
        )
    }) { padding ->
        Box(modifier.padding(padding)) {
            val pager = remember {
                Pager(PagingConfig(pageSize = 10)) {
                    WaifuPagingSource()
                }
            }

            val dismissedItems = remember { mutableStateListOf<String>() }
            val items = pager.flow.collectAsLazyPagingItems()
            when (items.loadState.refresh) {
                LoadState.Loading -> {
                    Box {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> {
                    Box {
                        Text(text = "Oops!")
                    }
                }

                else -> {
                    BoxWithConstraints {
                        val scope = rememberCoroutineScope()

                        val topItem = items[dismissedItems.size]

                        val topItemState = rememberDismissibleState(
                            containerWidthPx = with(LocalDensity.current) { maxWidth.toPx() },
                            containerHeightPx = with(LocalDensity.current) { maxHeight.toPx() },
                            onDismiss = {
                                dismissedItems.add(topItem!!) //TODO nullability
                                scope.launch {
                                    reset(null)
                                }
                            }
                        )

                        SwipeableStack(
                            topItemState = topItemState,
                            items = items,
                            dismissedItems = dismissedItems,
                        ) { item ->
                            WaifuCard(
                                url = item,
                                modifier = Modifier.onGloballyPositioned {
                                    topCardBounds = it.boundsInRoot()
                                }
                            )
                        }
                        val dislikeButtonScale by remember {
                            derivedStateOf {
                                getButtonScale(topItemState.horizontalDismissProgress * -1)
                            }
                        }
                        val likeButtonScale by remember {
                            derivedStateOf {
                                getButtonScale(topItemState.horizontalDismissProgress)
                            }
                        }

                        val animatedDislikeButtonScale by animateFloatAsState(dislikeButtonScale)
                        val animatedLikeButtonScale by animateFloatAsState(likeButtonScale)

                        Row(
                            Modifier
                                .padding(bottom = 48.dp)
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            FeedButton(
                                onClick = {
                                    scope.launch {
                                        topItemState.dismiss(Direction.Start)
                                    }
                                },
                                modifier = Modifier.graphicsLayer {
                                    scaleX = animatedDislikeButtonScale
                                    scaleY = animatedDislikeButtonScale
                                }
                            ) {
                                DislikeIcon()
                            }
                            Spacer(Modifier.width(72.dp))
                            FeedButton(
                                onClick = {
                                    scope.launch {
                                        topItemState.dismiss(Direction.End)
                                    }
                                },
                                modifier = Modifier.graphicsLayer {
                                    scaleX = animatedLikeButtonScale
                                    scaleY = animatedLikeButtonScale
                                }
                            ) {
                                LikeIcon()
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getButtonScale(dismissProgress: Float): Float {
    val minProgress = 0f
    val maxProgress = 0.5f
    val minScale = 0.8f
    val maxScale = 1f

    return when {
        dismissProgress < minProgress -> minScale
        dismissProgress > maxProgress -> maxScale
        else -> dismissProgress.scale(
            oldMin = minProgress, oldMax = maxProgress,
            newMin = minScale, newMax = maxScale,
        )
    }
}

@Composable
fun FeedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .border(
                width = 2.dp,
                color = Color.White,
                shape = CircleShape
            )
            .background(
                color = Color.Black.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .size(80.dp),
        enabled = enabled,
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            content()
        }
    }
}

@Composable
private fun DislikeIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Rounded.Close,
        contentDescription = stringResource(R.string.nope),
        modifier = modifier.size(32.dp),
        tint = Color.White
    )
}

@Composable
private fun LikeIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Rounded.Favorite,
        contentDescription = stringResource(R.string.like),
        modifier = modifier.size(32.dp),
        tint = Color.White
    )
}

@Composable
fun ColorCard(color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier.padding(16.dp),
    ) {
        Image(
            painter = ColorPainter(color),
            contentDescription = null,
            Modifier.padding(8.dp)
        )
    }
}

@Composable
fun WaifuCard(url: String, modifier: Modifier = Modifier) {
    Card(
        Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .then(modifier)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

//TODO this composable is kind of useless now, consider removing
@Composable
fun <T : Any> SwipeableStack(
    topItemState: DismissibleState,
    items: LazyPagingItems<T>,
    dismissedItems: MutableList<T>, //TODO avoid using MutableList
    modifier: Modifier = Modifier,
    content: @Composable (T) -> Unit
) = Box(modifier) {
    val topItem = items[dismissedItems.size]
    val backgroundItem = items[dismissedItems.size + 1]

    if (backgroundItem != null) {
        content(backgroundItem)
    }

    if (topItem != null) {
        Box(
            Modifier.dismissible(
                state = topItemState,
                directions = arrayOf(Direction.Start, Direction.End)
            )
        ) {
            content(topItem)
        }
    }
}

class RandomColorPagingSource : PagingSource<Int, Color>() {
    override fun getRefreshKey(state: PagingState<Int, Color>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Color> {
        val random = Random(params.key ?: 0)
        return LoadResult.Page(
            data = List(10) {
                Color(
                    random.nextInt(255),
                    random.nextInt(255),
                    random.nextInt(255)
                )
            },
            prevKey = null,
            nextKey = (params.key ?: 0) + params.loadSize
        )
    }
}

class WaifuPagingSource : PagingSource<Int, String>() {
    override fun getRefreshKey(state: PagingState<Int, String>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        return LoadResult.Page(
            data = List(10) {
                "https://www.thiswaifudoesnotexist.net/example-${Random.nextInt(100000 + 1)}.jpg"
            },
            prevKey = null,
            nextKey = (params.key ?: 0) + params.loadSize
        )
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    Yfoo2Theme {
        Greeting("Android")
    }
}