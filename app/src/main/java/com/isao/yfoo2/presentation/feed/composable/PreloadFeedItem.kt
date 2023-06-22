package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import coil.imageLoader
import coil.request.ImageRequest
import com.isao.yfoo2.presentation.feed.model.FeedItemDisplayable
import com.isao.yfoo2.presentation.transformations.BorderCropTransformation

@Composable
fun PreloadFeedItem(
    item: FeedItemDisplayable?,
    width: Dp,
    height: Dp,
) {
    val context = LocalContext.current
    val widthPx = with(LocalDensity.current) { width.roundToPx() }
    val heightPx = with(LocalDensity.current) { height.roundToPx() }

    LaunchedEffect(item) {
        if (item == null) return@LaunchedEffect
        context.imageLoader.enqueue(
            ImageRequest.Builder(context)
                .data(item.imageUrl)
                .size(
                    widthPx,
                    heightPx
                )
                .transformations(BorderCropTransformation())
                .build()
        )
    }
}