package com.isao.yfoo2.presentation.liked.composable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.isao.yfoo2.presentation.model.LikedImageDisplayable
import com.isao.yfoo2.presentation.transformations.BorderCropTransformation

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LikedItem(
    item: LikedImageDisplayable,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current).apply {
            data(item.imageUrl)
            transformations(BorderCropTransformation())
        }.build(),
        contentScale = ContentScale.Crop,
        contentDescription = null,
        modifier = modifier.combinedClickable(
            onClick = onClick,
            onLongClick = onLongClick,
        )
    )
}