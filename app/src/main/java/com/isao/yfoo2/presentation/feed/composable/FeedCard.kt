package com.isao.yfoo2.presentation.feed.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.isao.yfoo2.core.utils.CatPreviewPlaceholder
import com.isao.yfoo2.core.utils.debugPlaceholder
import com.isao.yfoo2.presentation.feed.model.FeedItemDisplayable
import com.isao.yfoo2.presentation.transformations.BitmapTransformations
import kotlinx.coroutines.delay
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.time.Duration.Companion.seconds

@Composable
fun FeedCard(
    painter: AsyncImagePainter?,
    modifier: Modifier = Modifier
) = Card(modifier) {
    if (LocalInspectionMode.current) {
        CatPreviewPlaceholder(Modifier.fillMaxSize())
        return@Card
    }

    val isAnyErrorExceptBadInternet = painter?.state?.let {
        it is AsyncImagePainter.State.Error
                && it.result.throwable !is SocketTimeoutException
                && it.result.throwable !is UnknownHostException
    }
    AnimatedVisibility(
        visible = isAnyErrorExceptBadInternet == true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        ErrorPlaceholder()
    }
    Image(
        painter = painter ?: rememberDrawablePainter(null),
        contentDescription = null,
        modifier = Modifier
            .fillMaxSize()
            .placeholder(
                visible = painter?.state !is AsyncImagePainter.State.Success,
                highlight = PlaceholderHighlight.shimmer()
            ),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun FeedCard(
    item: FeedItemDisplayable?,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    FeedCard(
        painter = if (item?.imageUrl != null) {
            FeedCardDefaults.rememberRetryingAsyncImagePainter(
                item = item,
                width = width,
                height = height
            )
        } else {
            null
        },
        modifier = modifier
    )
}

object FeedCardDefaults {
    @Composable
    fun rememberRetryingAsyncImagePainter(
        item: FeedItemDisplayable,
        width: Dp,
        height: Dp,
        error: Painter? = null,
        fallback: Painter? = error,
        onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
        onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
        onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
        contentScale: ContentScale = ContentScale.Crop,
        filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
    ): AsyncImagePainter {
        // Reloading the image on failure the ugly way. Open issue in Coil since 2021:
        // https://github.com/coil-kt/coil/issues/884#issuecomment-975932886
        var retryHash by remember(item.imageUrl) { mutableIntStateOf(0) }
        val painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(item.imageUrl)
                .setParameter("retryHash", retryHash)
                // The size has to be provided since we rely on AsyncImagePager.state for the placeholder
                // https://coil-kt.github.io/coil/compose/#observing-asyncimagepainterstate
                .size(
                    with(LocalDensity.current) { width.roundToPx() },
                    with(LocalDensity.current) { height.roundToPx() }
                )
                // By default retryHash is also included in keys.
                // This results in a bit longer loading if the same image is requested
                // with retryHash == 0 next time.
                // Set our own cache keys to avoid it.
                .diskCacheKey(item.imageUrl)
                .memoryCacheKey(item.imageUrl)
                .transformations(BitmapTransformations.getFor(item.source))
                .build(),
            placeholder = debugPlaceholder(Color.Magenta),
            contentScale = contentScale,
            error = error,
            fallback = fallback,
            onLoading = onLoading,
            onSuccess = onSuccess,
            onError = onError,
            filterQuality = filterQuality
        )

        val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
        val isAtLeastResumed = lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
        val hasImageRequestFailed = painter.state is AsyncImagePainter.State.Error
        LaunchedEffect(isAtLeastResumed, hasImageRequestFailed) {
            if (isAtLeastResumed && hasImageRequestFailed) {
                delay(if (retryHash <= 2) 2.seconds else 5.seconds)
                retryHash++
            }
        }

        return painter
    }
}

@Composable
private fun ErrorPlaceholder() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.2f),
            tint = MaterialTheme.colorScheme.error
        )
    }
}