package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
            error = rememberVectorPainter(Icons.Default.ErrorOutline),
            contentScale = ContentScale.Crop,
        )
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .placeholder(
                    visible = painter.state is AsyncImagePainter.State.Loading || imageUrl == null,
                    highlight = PlaceholderHighlight.shimmer()
                ),
            contentScale = ContentScale.Crop
        )
    }
}