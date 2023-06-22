package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.isao.yfoo2.R
import com.isao.yfoo2.core.extensions.scale
import com.isao.yfoo2.presentation.composable.Direction
import com.isao.yfoo2.presentation.composable.dismissible
import com.isao.yfoo2.presentation.composable.rememberDismissibleState
import com.isao.yfoo2.presentation.feed.FeedIntent
import com.isao.yfoo2.presentation.feed.FeedUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun CardFeed(
    uiState: FeedUiState,
    onIntent: (FeedIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier.fillMaxSize()) {
        val scope = rememberCoroutineScope()

        val notDismissedItems = uiState.items.filterNot { it.isDismissed }

        val preloadedItem = notDismissedItems.getOrNull(2)
        val backgroundItem = notDismissedItems.getOrNull(1)
        val topItem = notDismissedItems.getOrNull(0)

        val topItemState = rememberDismissibleState(
            onDismiss = { direction ->
                onIntent(
                    when (direction) {
                        Direction.Start -> FeedIntent.Dislike(topItem!!) //TODO nullability
                        Direction.End -> FeedIntent.Like(topItem!!) //TODO nullability
                        else -> throw IllegalArgumentException()
                    }
                )
                scope.launch {
                    reset(null)
                }
            }
        )

        PreloadFeedItem(
            item = preloadedItem,
            width = maxWidth,
            height = maxHeight
        )

        if (backgroundItem != null) {
            FeedCard(
                imageUrl = backgroundItem.imageUrl,
                width = maxWidth,
                height = maxHeight,
            )
        }

        FeedCard(
            imageUrl = topItem?.imageUrl,
            width = maxWidth,
            height = maxHeight,
            modifier = Modifier.dismissible(
                state = topItemState,
                directions = setOf(Direction.Start, Direction.End),
                enabled = topItem != null,
                containerWidth = maxWidth,
                containerHeight = maxHeight,
            )
        )

        val dislikeButtonScale by remember {
            derivedStateOf {
                getButtonScale(topItemState.horizontalDismissProgress * -1)
            }
        }
        val likeButtonScale by remember {
            derivedStateOf {
                getButtonScale(topItemState.horizontalDismissProgress)
            }
        }

        val animatedDislikeButtonScale by animateFloatAsState(dislikeButtonScale)
        val animatedLikeButtonScale by animateFloatAsState(likeButtonScale)

        Row(
            Modifier
                .padding(bottom = 48.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            var dismissCardJob by remember { mutableStateOf<Job?>(null) }
            FeedButton(
                onClick = {
                    scope.launch {
                        if (dismissCardJob?.isActive == true) return@launch
                        dismissCardJob = launch { topItemState.dismiss(Direction.Start) }
                    }
                },
                modifier = Modifier.graphicsLayer {
                    scaleX = animatedDislikeButtonScale
                    scaleY = animatedDislikeButtonScale
                },
                enabled = topItem != null
            ) {
                DislikeIcon()
            }
            Spacer(Modifier.width(72.dp))
            FeedButton(
                onClick = {
                    scope.launch {
                        if (dismissCardJob?.isActive == true) return@launch
                        dismissCardJob = launch { topItemState.dismiss(Direction.End) }
                    }
                },
                modifier = Modifier.graphicsLayer {
                    scaleX = animatedLikeButtonScale
                    scaleY = animatedLikeButtonScale
                },
                enabled = topItem != null
            ) {
                LikeIcon()
            }
        }
    }
}

@Composable
private fun FeedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.size(80.dp),
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Black.copy(alpha = 0.3f),
            contentColor = Color.White,
        ),
        border = BorderStroke(
            width = 2.dp,
            color = Color.White,
        )
    ) {
        content()
    }
}

@Composable
private fun DislikeIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Rounded.Close,
        contentDescription = stringResource(R.string.nope),
        modifier = modifier.size(32.dp),
        tint = Color.White
    )
}

@Composable
private fun LikeIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Rounded.Favorite,
        contentDescription = stringResource(R.string.like),
        modifier = modifier.size(32.dp),
        tint = Color.White
    )
}

private fun getButtonScale(dismissProgress: Float): Float {
    val minProgress = 0f
    val maxProgress = 0.5f
    val minScale = 0.8f
    val maxScale = 1f

    return when {
        dismissProgress < minProgress -> minScale
        dismissProgress > maxProgress -> maxScale
        else -> dismissProgress.scale(
            oldMin = minProgress, oldMax = maxProgress,
            newMin = minScale, newMax = maxScale,
        )
    }
}