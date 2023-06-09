package com.isao.yfoo2.presentation.composable

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs


//TODO support rtl
fun Modifier.dismissible(
    state: DismissibleState,
    directions: Array<Direction> = Direction.values(), //TODO replace with immutable list
    canDismiss: (Offset, Float, Float) -> Boolean = { _, horizontalProgress, verticalProgress ->
        abs(horizontalProgress) > 0.3f || abs(verticalProgress) > 0.3f
    }
) = pointerInput(Unit) {
    coroutineScope {
        val velocityTracker = VelocityTracker()
        detectDragGestures(
            onDragStart = { velocityTracker.resetTracking() },
            onDragEnd = {
                launch {
                    val coercedOffset = state.offset.targetValue.coerceIn(
                        allowedDirections = directions,
                        maxWidth = state.containerWidth,
                        maxHeight = state.containerHeight
                    )
                    val canDismissDueToPosition = canDismiss(
                        coercedOffset,
                        coercedOffset.x / state.containerWidth,
                        coercedOffset.y / state.containerHeight,
                    )

                    val coercedVelocity = velocityTracker.calculateVelocity().coerceIn(
                        allowedDirections = directions,
                        maxWidth = state.containerWidth,
                        maxHeight = state.containerHeight
                    )
                    val canDismissDueToVelocity =
                        coercedVelocity.x > state.dismissVelocity
                                || coercedVelocity.y > state.dismissVelocity

                    if (canDismissDueToPosition || canDismissDueToVelocity) {
                        val horizontalDismissProgress = state.horizontalDismissProgress
                        val verticalDismissProgress = state.verticalDismissProgress
                        val direction =
                            if (abs(horizontalDismissProgress) > abs(verticalDismissProgress)) {
                                if (horizontalDismissProgress > 0) Direction.End else Direction.Start
                            } else {
                                if (verticalDismissProgress > 0) Direction.Down else Direction.Up
                            }
                        state.dismiss(direction)
                    } else {
                        state.reset()
                    }
                }
            },
            onDragCancel = {
                launch {
                    state.onDismissCancel()
                    state.reset()
                }
            },
            onDrag = { change: PointerInputChange, dragged: Offset ->
                launch {
                    change.consume()
                    velocityTracker.addPosition(change.uptimeMillis, change.position)
                    val original = state.offset.targetValue
                    val summed = original + dragged
                    state.drag(
                        x = summed.x.coerceIn(-state.containerWidth, state.containerWidth),
                        y = summed.y.coerceIn(-state.containerHeight, state.containerHeight)
                    )
                }
            })
    }
}.graphicsLayer {
    translationX = state.offset.value.x
    translationY = state.offset.value.y
    rotationZ = state.rotationZ
}

private fun Offset.coerceIn(
    allowedDirections: Array<Direction>,
    maxWidth: Float,
    maxHeight: Float,
): Offset = copy(
    x = x.coerceWidthIn(allowedDirections, maxWidth),
    y = y.coerceHeightIn(allowedDirections, maxHeight)
)

private fun Velocity.coerceIn(
    allowedDirections: Array<Direction>,
    maxWidth: Float,
    maxHeight: Float,
): Velocity = copy(
    x = x.coerceWidthIn(allowedDirections, maxWidth),
    y = y.coerceHeightIn(allowedDirections, maxHeight)
)

private fun Float.coerceWidthIn(
    allowedDirections: Array<Direction>,
    maxWidth: Float,
): Float = coerceIn(
    if (allowedDirections.contains(Direction.Start)) -maxWidth else 0f,
    if (allowedDirections.contains(Direction.End)) maxWidth else 0f
)

private fun Float.coerceHeightIn(
    allowedDirections: Array<Direction>,
    maxHeight: Float,
): Float = coerceIn(
    if (allowedDirections.contains(Direction.Up)) -maxHeight else 0f,
    if (allowedDirections.contains(Direction.Down)) maxHeight else 0f,
)
