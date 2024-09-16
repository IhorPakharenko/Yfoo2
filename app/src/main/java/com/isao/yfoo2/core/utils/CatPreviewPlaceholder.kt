package com.isao.yfoo2.core.utils

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.isao.yfoo2.R

@Composable
fun CatPreviewPlaceholder(modifier: Modifier = Modifier) = Image(
    painter = painterResource(R.drawable.placeholder_cat),
    contentDescription = null,
    modifier = modifier,
    contentScale = ContentScale.Crop
)