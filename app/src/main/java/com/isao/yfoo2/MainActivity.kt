package com.isao.yfoo2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.isao.yfoo2.ui.Direction
import com.isao.yfoo2.ui.dismissible
import com.isao.yfoo2.ui.rememberDismissibleState
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
    ) = BoxWithConstraints(modifier) {
        val scope = rememberCoroutineScope()

        val topItem = items[dismissedItems.size]
        val backgroundItem = items[dismissedItems.size + 1]

        if (backgroundItem != null) {
            content(backgroundItem)
        }

        val topItemState = rememberDismissibleState(
            containerWidthPx = with(LocalDensity.current) { maxWidth.toPx() },
            containerHeightPx = with(LocalDensity.current) { maxHeight.toPx() },
        )

        if (topItem != null) {
            Box(Modifier.dismissible(
                state = topItemState,
                directions = arrayOf(Direction.Start, Direction.End),
                onDismiss = {
                    dismissedItems.add(topItem)
                    scope.launch {
                        topItemState.reset(null)
                    }
                },
                onDismissCancel = {}
            )) {
                content(topItem)
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