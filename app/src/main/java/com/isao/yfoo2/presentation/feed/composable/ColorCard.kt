package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.unit.dp

@Composable
private fun ColorCard(color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier.padding(16.dp),
    ) {
        Image(
            painter = ColorPainter(color),
            contentDescription = null,
            Modifier.padding(8.dp)
        )
    }
}