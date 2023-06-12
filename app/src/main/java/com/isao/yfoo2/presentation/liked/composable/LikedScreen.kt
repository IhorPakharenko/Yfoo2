package com.isao.yfoo2.presentation.liked.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.isao.yfoo2.R
import com.isao.yfoo2.domain.model.ImageSource
import com.isao.yfoo2.presentation.liked.LikedIntent
import com.isao.yfoo2.presentation.liked.LikedUiState
import com.isao.yfoo2.presentation.liked.LikedViewModel
import com.isao.yfoo2.presentation.transformations.BorderCropTransformation

@Composable
fun LikedRoute(
    viewModel: LikedViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LikedScreen(uiState = uiState, onIntent = viewModel::acceptIntent)

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun LikedScreen(
    uiState: LikedUiState,
    onIntent: (LikedIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val topBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(topBarState)

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(stringResource(R.string.app_name))
            },
            scrollBehavior = scrollBehavior
        )
    }) { padding ->
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Adaptive(100.dp),
            modifier = modifier
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(16.dp),
            verticalItemSpacing = 16.dp,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(uiState.items) {
                Card(
                    onClick = { /*TODO*/ },
                ) {
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current).apply {
                            data(it.imageUrl)
                            size(Size.ORIGINAL)
                            if (it.source == ImageSource.THIS_WAIFU_DOES_NOT_EXIST) {
                                transformations(BorderCropTransformation())
                            }
                        }.build()
                    )
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}