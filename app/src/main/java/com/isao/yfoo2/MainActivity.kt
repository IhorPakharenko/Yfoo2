package com.isao.yfoo2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.paging.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.isao.yfoo2.ui.theme.Yfoo2Theme
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Yfoo2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val pager = remember {
                        Pager(PagingConfig(pageSize = 10)) {
                            RandomColorPagingSource()
                        }
                    }


                    val dismissedItems = remember { mutableStateListOf<Color>() }
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
                            SwipeableStack(
                                items = items,
                                dismissedItems = dismissedItems,
                            ) { color ->
                                Card(
                                    Modifier.padding(16.dp),
                                ) {
                                    Image(
                                        painter = ColorPainter(color),
                                        contentDescription = null,
                                        Modifier.padding(8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun <T : Any> SwipeableStack(
        items: LazyPagingItems<T>,
        dismissedItems: MutableList<T>,
        modifier: Modifier = Modifier,
        content: @Composable (T) -> Unit
    ) {
        BoxWithConstraints(modifier) {
            val topItem = items[dismissedItems.size]
            val backgroundItem = items[dismissedItems.size + 1]

            val maxCardSwipeDistanceDp = maxWidth

            var dummyCardSwipeProgress by remember { mutableStateOf(0f) }
            val dummyCardOffsetDp = remember {
                derivedStateOf {
                    maxCardSwipeDistanceDp * dummyCardSwipeProgress
                }
            }
            var prevItem by remember { mutableStateOf<T?>(null) }

            val scope = rememberCoroutineScope()
            SideEffect {
                if (prevItem != topItem) {
                    prevItem = topItem
                    scope.launch {
                        animate(
                            0f,
                            1f,
                            animationSpec = spring(stiffness = Spring.StiffnessLow)
                        ) { value, _ ->
                            dummyCardSwipeProgress = value
                        }
                    }
                }
            }

            if (backgroundItem != null) {
                content(backgroundItem)
            }
            if (topItem != null) {
                Box(Modifier.clickable { dismissedItems.add(topItem) }) {
                    content(topItem)
                }
            }
            val dismissedItem = dismissedItems.lastOrNull()
            if (dismissedItem != null) {
                Box(Modifier.offset { IntOffset(dummyCardOffsetDp.value.roundToPx(), 0) }
                ) {
                    content(dismissedItem)
                }
            }
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