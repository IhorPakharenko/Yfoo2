package com.isao.yfoo2.presentation.liked.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.isao.yfoo2.core.utils.CatPreviewPlaceholder
import com.isao.yfoo2.core.utils.debugPlaceholder
import com.isao.yfoo2.presentation.liked.model.LikedImageDisplayable
import com.isao.yfoo2.presentation.transformations.BorderCropTransformation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LikedItem(
    item: LikedImageDisplayable,
    width: Dp,
    height: Dp,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (LocalInspectionMode.current) {
        CatPreviewPlaceholder()
        return
    }
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(item.imageUrl)
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
    if (painter.state !is AsyncImagePainter.State.Error) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = modifier
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick,
                )
                .placeholder(
                    visible = painter.state is AsyncImagePainter.State.Loading,
                    highlight = PlaceholderHighlight.shimmer()
                ),
            contentScale = ContentScale.Crop,
        )
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.5f),
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}