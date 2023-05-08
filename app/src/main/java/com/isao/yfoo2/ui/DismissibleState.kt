package com.isao.yfoo2.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.abs
import kotlin.math.sin


enum class Direction {
    Start, End, Up, Down
}

@Composable
fun rememberDismissibleState(containerWidthPx: Float, containerHeightPx: Float): DismissibleState {
    val layoutDirection = LocalLayoutDirection.current
    return remember {
        DismissibleState(containerWidthPx, containerHeightPx, layoutDirection)
    }
}


class DismissibleState(
    val containerWidth: Float,
    val containerHeight: Float,
    val maxRotationZ: Float,
    private val layoutDirection: LayoutDirection
) {
    val offset = Animatable(offset(0f, 0f), Offset.VectorConverter)

    val endX = getEndX(containerWidth = containerWidth, containerHeight = containerHeight).toFloat()
    val endY = getEndY(containerWidth = containerWidth, containerHeight = containerHeight).toFloat()

    /**
     * The [Direction] the composable was swiped at.
     *
     * Null value means the composable has not been swiped fully yet.
     */
    var dismissedDirection: Direction? by mutableStateOf(null)
        private set

    internal suspend fun reset() {
        offset.animateTo(offset(0f, 0f), tween(400))
    }

    suspend fun dismiss(direction: Direction, spec: AnimationSpec<Offset> = tween(400)) {
//        val endX = containerWidth * 1.5f
//        val endY = containerHeight
        val directionMultiplier = if (layoutDirection == LayoutDirection.Rtl) -1 else 1
        when (direction) {
            Direction.Start -> offset.animateTo(offset(x = -endX * directionMultiplier), spec)
            Direction.End -> offset.animateTo(offset(x = endX * directionMultiplier), spec)
            Direction.Up -> offset.animateTo(offset(y = -endY), spec)
            Direction.Down -> offset.animateTo(offset(y = endY), spec)
        }
        this.dismissedDirection = direction
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
