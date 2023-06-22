package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Size
import com.isao.yfoo2.core.utils.debugPlaceholder
import com.isao.yfoo2.presentation.transformations.BorderCropTransformation

@Composable
fun ImageCard(url: String?, modifier: Modifier = Modifier) {
    Card(
        Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .then(modifier)
    ) {
//        var shouldShowPlaceholder by remember { mutableStateOf(false) }
        AsyncImage( //TODO placeholder when loading / no internet
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .size(Size.ORIGINAL)
                .transformations(BorderCropTransformation())
                .build(),
            contentDescription = null,
//            onState = {
//                shouldShowPlaceholder = it is AsyncImagePainter.State.Success
//            },
            placeholder = debugPlaceholder(Color.Magenta),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}