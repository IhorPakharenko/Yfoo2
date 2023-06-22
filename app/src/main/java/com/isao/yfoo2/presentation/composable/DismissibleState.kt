package com.isao.yfoo2.presentation.composable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Velocity
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sin


enum class Direction {
    Start, End, Up, Down
}

@Composable
fun rememberDismissibleState(
    onDismiss: DismissibleState.(Direction) -> Unit = {},
    onDismissCancel: () -> Unit = {}
): DismissibleState {
    val layoutDirection = LocalLayoutDirection.current
    val onDismissState = rememberUpdatedState(onDismiss)
    val onDismissCancelState = rememberUpdatedState(onDismissCancel)
    return remember { //TODO use rememberSaveable
        DismissibleState(
            layoutDirection,
            { onDismissState.value.invoke(this, it) },
            { onDismissCancelState.value.invoke() }
        )
    }
}

@Stable
class DismissibleState(
    private val layoutDirection: LayoutDirection,
    val onDismiss: DismissibleState.(Direction) -> Unit,
    val onDismissCancel: () -> Unit
) {
    private val offset = Animatable(Offset.Zero, Offset.VectorConverter)

    internal var directions: Set<Direction> by mutableStateOf(emptySet())
    internal var containerWidthPx: Float by mutableStateOf(0f)
    internal var containerHeightPx: Float by mutableStateOf(0f)
    internal var maxRotationZ: Float by mutableStateOf(0f)
    internal var velocityThreshold: Float by mutableStateOf(0f)
    internal var minHorizontalProgressThreshold: Float by mutableStateOf(0f)
    internal var minVerticalProgressThreshold: Float by mutableStateOf(0f)

    val value get() = offset.value
    val targetValue get() = offset.targetValue

    /**
     * Not coerced by directions
     */
    val horizontalDismissProgress by derivedStateOf {
        offset.value.x / containerWidthPx * if (layoutDirection == LayoutDirection.Rtl) -1 else 1
    }

    /**
     * Not coerced by directions
     */
    val verticalDismissProgress by derivedStateOf {
        offset.value.y / containerHeightPx
    }

    val rotationZ by derivedStateOf {
        maxRotationZ * offset.value.x / containerWidthPx
    }

    /**
     * The [Direction] the composable was swiped at.
     *
     * Null value means the composable has not been swiped fully yet.
     */
    var dismissedDirection: Direction? by mutableStateOf(null)
        private set

    private val endX by derivedStateOf {
        getEndX(containerWidth = containerWidthPx, containerHeight = containerHeightPx).toFloat()
    }
    private val endY by derivedStateOf {
        getEndY(containerWidth = containerWidthPx, containerHeight = containerHeightPx).toFloat()
    }

    suspend fun reset(
        animationSpec: AnimationSpec<Offset>? = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    ) {
        dismissedDirection = null
        if (animationSpec != null) {
            offset.animateTo(Offset.Zero, animationSpec)
        } else {
            offset.snapTo(Offset.Zero)
        }
    }

    suspend fun dismiss(direction: Direction, spec: AnimationSpec<Offset> = tween(500)) {
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

    internal suspend fun performFling(velocity: Velocity) {
        val directionMultiplier =
            if (layoutDirection == LayoutDirection.Rtl) -1 else 1

        val coercedVelocity = velocity.coerceIn(
            allowedDirections = directions
        )
        val dismissDirectionDueToVelocity = getDismissDirection(
            valueX = coercedVelocity.x * directionMultiplier,
            valueY = coercedVelocity.y,
            minValueX = velocityThreshold,
            minValueY = velocityThreshold,
        )

        val coercedOffset = offset.targetValue.coerceIn(
            allowedDirections = directions,
            maxWidth = containerWidthPx,
            maxHeight = containerHeightPx
        )
        val dismissDirectionDueToOffset = getDismissDirection(
            valueX = coercedOffset.x * directionMultiplier,
            valueY = coercedOffset.y,
            minValueX = containerWidthPx * minHorizontalProgressThreshold,
            minValueY = containerHeightPx * minVerticalProgressThreshold,
        )

        val dismissDirection: Direction? =
            dismissDirectionDueToVelocity ?: dismissDirectionDueToOffset
        if (dismissDirection != null) {
            dismiss(dismissDirection)
        } else {
            reset()
        }
    }

    internal suspend fun performDrag(dragged: Offset) {
        val original = offset.targetValue
        val summed = original + dragged
        offset.animateTo(
            offset(
                x = summed.x.coerceIn(-containerWidthPx, containerWidthPx),
                y = summed.y.coerceIn(-containerHeightPx, containerHeightPx)
            )
        )
    }

    private fun offset(x: Float = offset.value.x, y: Float = offset.value.y): Offset {
        return Offset(x, y)
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

    private fun Offset.coerceIn(
        allowedDirections: Set<Direction>,
        maxWidth: Float,
        maxHeight: Float,
    ): Offset = copy(
        x = x.coerceWidthIn(allowedDirections, maxWidth),
        y = y.coerceHeightIn(allowedDirections, maxHeight)
    )

    private fun Velocity.coerceIn(
        allowedDirections: Set<Direction>
    ): Velocity = copy(
        x = x.coerceWidthIn(allowedDirections, Float.MAX_VALUE),
        y = y.coerceHeightIn(allowedDirections, Float.MAX_VALUE)
    )

    private fun Float.coerceWidthIn(
        allowedDirections: Set<Direction>,
        maxWidth: Float,
    ): Float = coerceIn(
        if (allowedDirections.contains(Direction.Start)) -maxWidth else 0f,
        if (allowedDirections.contains(Direction.End)) maxWidth else 0f
    )

    private fun Float.coerceHeightIn(
        allowedDirections: Set<Direction>,
        maxHeight: Float,
    ): Float = coerceIn(
        if (allowedDirections.contains(Direction.Up)) -maxHeight else 0f,
        if (allowedDirections.contains(Direction.Down)) maxHeight else 0f,
    )

    /**
     * Finds the direction depending on which value is closest to its corresponding minValue
     * @return The direction of the dismiss action
     * or null if not the conditions for the dismiss have not been met
     */
    private fun getDismissDirection(
        valueX: Float,
        valueY: Float,
        minValueX: Float,
        minValueY: Float
    ): Direction? {
        return listOf(
            Direction.End to valueX / minValueX,
            Direction.Start to valueX.absoluteValue / minValueX,
            Direction.Down to valueY / minValueY,
            Direction.Up to valueY.absoluteValue / minValueY,
        )
            .sortedBy { (_, ratio) -> ratio }
            .firstOrNull { (_, ratio) ->
                ratio >= 1
            }
            ?.first
    }
}
