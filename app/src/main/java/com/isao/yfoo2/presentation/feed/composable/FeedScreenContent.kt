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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import com.isao.yfoo2.R
import com.isao.yfoo2.core.extensions.findActivity
import com.isao.yfoo2.core.extensions.scale
import com.isao.yfoo2.core.utils.SplashScreenHost
import com.isao.yfoo2.presentation.composable.dismissible.DismissDirection
import com.isao.yfoo2.presentation.composable.dismissible.DismissibleState
import com.isao.yfoo2.presentation.composable.dismissible.dismissible
import com.isao.yfoo2.presentation.composable.dismissible.rememberDismissibleState
import com.isao.yfoo2.presentation.feed.FeedIntent
import com.isao.yfoo2.presentation.feed.FeedUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

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
                item = backgroundItem,
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
        val topItemPainter = topItem?.let { item ->
            FeedCardDefaults.rememberRetryingAsyncImagePainter(
                item = item,
                width = cardImageWidth,
                height = cardImageHeight
            ).also {
                SplashController(painterState = it.state)
            }
        }

        val isLikeAllowed by remember(topItem) {
            derivedStateOf {
                topItemPainter?.state is AsyncImagePainter.State.Success
            }
        }
        val isDislikeAllowed by remember(topItem) {
            derivedStateOf {
                topItemPainter?.state != null
            }
        }

        FeedCard(
            painter = topItemPainter,
            modifier = Modifier
                .padding(cardPadding)
                .dismissible(
                    state = topItemState,
                    directions = setOfNotNull(
                        DismissDirection.Start.takeIf { isDislikeAllowed },
                        DismissDirection.End.takeIf { isLikeAllowed }
                    ),
                )
        )

        FeedButtons(
            topItemState = topItemState,
            isLikeEnabled = isLikeAllowed,
            isDislikeEnabled = isDislikeAllowed,
            modifier = Modifier
                .padding(bottom = 48.dp)
                .align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FeedButtons(
    topItemState: DismissibleState,
    isLikeEnabled: Boolean,
    isDislikeEnabled: Boolean,
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

    // Dislike button
    FeedButton(
        onClick = {
            scope.launch {
                if (topItemState.dismissDirection != null) return@launch
                launch { topItemState.dismiss(DismissDirection.Start) }
            }
        },
        modifier = Modifier.graphicsLayer {
            scaleX = dislikeButtonScale
            scaleY = dislikeButtonScale
        },
        enabled = isDislikeEnabled
    ) {
        DislikeIcon()
    }

    Spacer(Modifier.width(72.dp))

    // Like button
    FeedButton(
        onClick = {
            scope.launch {
                if (topItemState.dismissDirection != null) return@launch
                launch { topItemState.dismiss(DismissDirection.End) }
            }
        },
        modifier = Modifier.graphicsLayer {
            scaleX = likeButtonScale
            scaleY = likeButtonScale
        },
        enabled = isLikeEnabled
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

/**
 * Keeps splash screen on screen for an extra second or until the first image is prepared.
 * Does nothing if splash screen is not allowed to be drawn longer than necessary
 * ([SplashScreenHost.shouldKeepSplashScreen] is false)
 */
@OptIn(FlowPreview::class)
@Composable
private fun SplashController(painterState: AsyncImagePainter.State) {
    val splashScreenHost = LocalContext.current.findActivity() as? SplashScreenHost
    if (splashScreenHost?.shouldKeepSplashScreen != true) return
    LaunchedEffect(Unit) {
        snapshotFlow { painterState }
            .map {
                it is AsyncImagePainter.State.Success
                        || it is AsyncImagePainter.State.Error
            }
            .timeout(1.seconds)
            .catch { emit(true) }
            .filter { it }
            .collect {
                splashScreenHost.shouldKeepSplashScreen = false
            }
    }
}