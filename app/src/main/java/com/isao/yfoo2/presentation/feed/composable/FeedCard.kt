package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.isao.yfoo2.core.utils.debugPlaceholder
import com.isao.yfoo2.presentation.transformations.BorderCropTransformation

@Composable
fun FeedCard(
    imageUrl: String?,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    Card(
        Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .then(modifier)
    ) {
        if (imageUrl == null) {
            EmptyPlaceholder()
            return@Card
        }

        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                // The size has to be provided since we rely on AsyncImagePager.state for the placeholder
                // https://coil-kt.github.io/coil/compose/#observing-asyncimagepainterstate
                .size(
                    with(LocalDensity.current) { width.roundToPx() },
                    with(LocalDensity.current) { height.roundToPx() }
                )
                .transformations(BorderCropTransformation())
                .build(),
            placeholder = debugPlaceholder(Color.Magenta),
            contentScale = ContentScale.Crop,
        )

        when (painter.state) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            is AsyncImagePainter.State.Loading, AsyncImagePainter.State.Empty -> {
                EmptyPlaceholder()
            }

            is AsyncImagePainter.State.Error -> {
                ErrorPlaceholder()
            }
        }
    }
}

@Composable
private fun EmptyPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .placeholder(
                visible = true,
                highlight = PlaceholderHighlight.shimmer()
            )
    )
}

@Composable
private fun ErrorPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.2f),
            tint = MaterialTheme.colorScheme.error
        )
    }
}