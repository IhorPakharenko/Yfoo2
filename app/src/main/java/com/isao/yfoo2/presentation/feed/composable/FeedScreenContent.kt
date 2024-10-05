package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import coil.compose.AsyncImagePainter
import com.isao.yfoo2.R
import com.isao.yfoo2.core.extensions.scale
import com.isao.yfoo2.presentation.composable.dismissible.DismissDirection
import com.isao.yfoo2.presentation.composable.dismissible.DismissibleState
import com.isao.yfoo2.presentation.composable.dismissible.dismissible
import com.isao.yfoo2.presentation.composable.dismissible.rememberDismissibleState
import com.isao.yfoo2.presentation.feed.FeedIntent
import com.isao.yfoo2.presentation.feed.FeedUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun FeedScreenContent(
    uiState: FeedUiState,
    onIntent: (FeedIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier) {
        val scope = rememberCoroutineScope()

        val preloadedItem = uiState.items.getOrNull(2)
        val backgroundItem = uiState.items.getOrNull(1)
        val topItem = uiState.items.getOrNull(0)

        val topItemState = rememberDismissibleState(
            onDismiss = { direction ->
                topItem ?: return@rememberDismissibleState
                onIntent(
                    when (direction) {
                        DismissDirection.Start -> FeedIntent.Dislike(topItem)
                        DismissDirection.End -> FeedIntent.Like(topItem)
                        else -> throw IllegalArgumentException()
                    }
                )
                scope.launch {
                    reset(null)
                }
            }
        )

        val horizontalCardPadding = 16.dp
        val verticalCardPadding = 32.dp
        val cardPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp)
        val cardImageWidth = maxWidth - horizontalCardPadding * 2
        val cardImageHeight = maxHeight - verticalCardPadding * 2

        PreloadFeedItem(
            item = preloadedItem,
            width = cardImageWidth,
            height = cardImageHeight
        )

        if (backgroundItem != null) {
            val backgroundCardTargetScale by remember {
                derivedStateOf {
                    topItemState.combinedDismissProgress
                        .coerceIn(0f..1f)
                        .scale(
                            oldMin = 0f, oldMax = 1f,
                            newMin = 0.95f, newMax = 1f,
                        )
                }
            }
            val backgroundCardScale by animateFloatAsState(backgroundCardTargetScale)

            FeedCard(
                imageUrl = backgroundItem.imageUrl,
                width = cardImageWidth,
                height = cardImageHeight,
                Modifier
                    .padding(cardPadding)
                    .graphicsLayer {
                        scaleX = backgroundCardScale
                        scaleY = backgroundCardScale
                    }
            )
        }

        val topItemPainter = topItem?.imageUrl?.let { url ->
            FeedCardDefaults.rememberRetryingAsyncImagePainter(
                imageUrl = url,
                width = cardImageWidth,
                height = cardImageHeight
            )
        }
        //TODO perhaps change this behavior. Right now, if the link to the top item is not working
        // for any reason, the user can not skip this image and view others, that are possibly working properly.
        // Maybe allow skipping but not allow liking broken images
        val isTopItemEnabled by remember(topItem) {
            derivedStateOf {
                topItemPainter?.state is AsyncImagePainter.State.Success
            }
        }

        FeedCard(
            painter = topItemPainter,
            modifier = Modifier
                .padding(cardPadding)
                .dismissible(
                    state = topItemState,
                    directions = if (isTopItemEnabled) {
                        setOf(DismissDirection.Start, DismissDirection.End)
                    } else {
                        emptySet()
                    },
                    enabled = isTopItemEnabled,
                )
        )

        FeedButtons(
            topItemState = topItemState,
            enabled = isTopItemEnabled,
            modifier = Modifier
                .padding(bottom = 48.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FeedButtons(
    topItemState: DismissibleState,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) = Row(modifier, horizontalArrangement = Arrangement.SpaceBetween) {
    val scope = rememberCoroutineScope()

    val dislikeButtonTargetScale by remember {
        derivedStateOf {
            getButtonScale(topItemState.horizontalDismissProgress * -1)
        }
    }
    val dislikeButtonScale by animateFloatAsState(dislikeButtonTargetScale)

    val likeButtonTargetScale by remember {
        derivedStateOf {
            getButtonScale(topItemState.horizontalDismissProgress)
        }
    }
    val likeButtonScale by animateFloatAsState(likeButtonTargetScale)

    var dismissAnimationJob by remember { mutableStateOf<Job?>(null) }

    FeedButton(
        onClick = {
            scope.launch {
                if (dismissAnimationJob?.isActive == true) return@launch
                dismissAnimationJob = launch { topItemState.dismiss(DismissDirection.Start) }
            }
        },
        modifier = Modifier.graphicsLayer {
            scaleX = dislikeButtonScale
            scaleY = dislikeButtonScale
        },
        enabled = enabled
    ) {
        DislikeIcon()
    }

    Spacer(Modifier.width(72.dp))

    FeedButton(
        onClick = {
            scope.launch {
                if (dismissAnimationJob?.isActive == true) return@launch
                dismissAnimationJob = launch { topItemState.dismiss(DismissDirection.End) }
            }
        },
        modifier = Modifier.graphicsLayer {
            scaleX = likeButtonScale
            scaleY = likeButtonScale
        },
        enabled = enabled
    ) {
        LikeIcon()
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

    if (dismissProgress.isNaN()) return minScale // Dismiss progress has not been initialized yet

    return when {
        dismissProgress < minProgress -> minScale
        dismissProgress > maxProgress -> maxScale
        else -> dismissProgress.scale(
            oldMin = minProgress, oldMax = maxProgress,
            newMin = minScale, newMax = maxScale,
        )
    }
}