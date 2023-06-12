package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.isao.yfoo2.presentation.transformations.BorderCropTransformation

@Composable
fun WaifuCard(url: String, modifier: Modifier = Modifier) {
    Card(
        Modifier
            .padding(horizontal = 16.dp, vertical = 32.dp)
            .then(modifier)
    ) {
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current).apply {
                data(url)
                size(Size.ORIGINAL)
                transformations(BorderCropTransformation())
            }.build()
        )
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}