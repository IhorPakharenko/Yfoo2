package com.isao.yfoo2.presentation.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.math.sin


enum class Direction {
    Start, End, Up, Down
}

@Composable
fun rememberDismissibleState(
    containerWidthPx: Float,
    containerHeightPx: Float,
    maxRotationZ: Float = 15f,
    //TODO it's pixels per second so it won't work the same way on different devices
    dismissVelocity: Float = 2000f,
    onDismiss: DismissibleState.(Direction) -> Unit = {},
    onDismissCancel: () -> Unit = {}
): DismissibleState {
    val layoutDirection = LocalLayoutDirection.current
    val onDismissState = rememberUpdatedState(onDismiss)
    val onDismissCancelState = rememberUpdatedState(onDismissCancel)
    return remember {
        DismissibleState(
            containerWidthPx,
            containerHeightPx,
            maxRotationZ,
            dismissVelocity,
            layoutDirection,
            { onDismissState.value.invoke(this, it) },
            { onDismissCancelState.value.invoke() }
        )
    }
}


class DismissibleState(
    val containerWidth: Float,
    val containerHeight: Float,
    val maxRotationZ: Float,
    val dismissVelocity: Float,
    private val layoutDirection: LayoutDirection,
    val onDismiss: DismissibleState.(Direction) -> Unit,
    val onDismissCancel: () -> Unit
) {
    val offset = Animatable(Offset.Zero, Offset.VectorConverter)

    val endX = getEndX(containerWidth = containerWidth, containerHeight = containerHeight).toFloat()
    val endY = getEndY(containerWidth = containerWidth, containerHeight = containerHeight).toFloat()

    //TODO derivedStateOf here is likely meaningless
    val horizontalDismissProgress by derivedStateOf {
        offset.value.x / containerWidth
    }
    val verticalDismissProgress by derivedStateOf {
        offset.value.y / containerHeight
    }

    val rotationZ by derivedStateOf {
        maxRotationZ * horizontalDismissProgress
    }

    /**
     * The [Direction] the composable was swiped at.
     *
     * Null value means the composable has not been swiped fully yet.
     */
    var dismissedDirection: Direction? by mutableStateOf(null)
        private set

    internal suspend fun reset(animationSpec: AnimationSpec<Offset>? = tween(400)) {
        dismissedDirection = null
        if (animationSpec != null) {
            offset.animateTo(Offset.Zero, animationSpec)
        } else {
            offset.snapTo(Offset.Zero)
        }
    }

    suspend fun dismiss(direction: Direction, spec: AnimationSpec<Offset> = tween(400)) {
        val directionMultiplier = if (layoutDirection == LayoutDirection.Rtl) -1 else 1
        when (direction) {
            Direction.Start -> offset.animateTo(offset(x = -endX * directionMultiplier), spec)
            Direction.End -> offset.animateTo(offset(x = endX * directionMultiplier), spec)
            Direction.Up -> offset.animateTo(offset(y = -endY), spec)
            Direction.Down -> offset.animateTo(offset(y = endY), spec)
        }
        this.dismissedDirection = direction
        onDismiss(direction)
    }

    private fun offset(x: Float = offset.value.x, y: Float = offset.value.y): Offset {
        return Offset(x, y)
    }

    suspend fun drag(x: Float, y: Float) {
        offset.animateTo(offset(x, y))
    }

    private fun getEndX(containerWidth: Float, containerHeight: Float): Double {
        val maxRotationsRadians = Math.toRadians(maxRotationZ.toDouble())
        val ninetyDegreesRadians = Math.toRadians(90.0)
        return abs(containerWidth * sin(ninetyDegreesRadians - maxRotationsRadians)) +
                abs(containerHeight * sin(maxRotationsRadians))
    }

    private fun getEndY(containerWidth: Float, containerHeight: Float): Double {
        val maxRotationsRadians = Math.toRadians(maxRotationZ.toDouble())
        val ninetyDegreesRadians = Math.toRadians(90.0)
        return abs(containerHeight * sin(ninetyDegreesRadians - maxRotationsRadians)) +
                abs(containerWidth * sin(maxRotationsRadians))
    }
}
