package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.isao.yfoo2.R
import com.isao.yfoo2.core.theme.Yfoo2Theme
import com.isao.yfoo2.presentation.feed.FeedIntent
import com.isao.yfoo2.presentation.feed.FeedUiState
import com.isao.yfoo2.presentation.feed.FeedViewModel

@Composable
fun FeedRoute(
    viewModel: FeedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    FeedScreen(uiState = uiState, onIntent = viewModel::acceptIntent)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    uiState: FeedUiState,
    onIntent: (FeedIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    var topAppBarBounds by remember { mutableStateOf<Rect?>(null) }
    var topCardBounds by remember { mutableStateOf<Rect?>(null) }
    val showTopAppBarDivider by remember {
        derivedStateOf {
            topCardBounds?.overlaps(topAppBarBounds ?: return@derivedStateOf false) == true
        }
    }
    //TODO move the card above the app bar
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
    },
        // Let the content take up all available space.
        // Material3 components handle the insets themselves
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(modifier.padding(padding)) {
            CardFeed(
                uiState,
                onIntent
            )
            //TODO loading and errors
        }
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