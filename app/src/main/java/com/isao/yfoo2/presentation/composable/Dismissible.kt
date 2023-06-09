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
import kotlin.math.absoluteValue


//TODO support rtl
//TODO decide which parameters go to state and which go here
fun Modifier.dismissible(
    state: DismissibleState,
    directions: Array<Direction> = Direction.values(), //TODO replace with immutable list
    minHorizontalProgressToDismiss: Float = 0.5f,
    minVerticalProgressToDismiss: Float = 0.5f,
) = pointerInput(Unit) {
    check(minHorizontalProgressToDismiss > 0f && minHorizontalProgressToDismiss <= 1) {
        "minHorizontalProgressToDismiss must be greater than 0 and less than or equal to 1"
    }
    check(minVerticalProgressToDismiss > 0f && minVerticalProgressToDismiss <= 1) {
        "minVerticalProgressToDismiss must be greater than 0 and less than or equal to 1"
    }

    coroutineScope {
        val velocityTracker = VelocityTracker()
        detectDragGestures(
            onDragStart = { velocityTracker.resetTracking() },
            onDragEnd = {
                launch {
                    val coercedVelocity = velocityTracker.calculateVelocity().coerceIn(
                        allowedDirections = directions
                    )
                    //TODO coercing to maxWidth and maxHeight might be pointless
                    val coercedOffset = state.offset.targetValue.coerceIn(
                        allowedDirections = directions,
                        maxWidth = state.containerWidth,
                        maxHeight = state.containerHeight
                    )
                    val dismissDirection: Direction? =
                        getDismissDirection(
                            valueX = coercedVelocity.x,
                            valueY = coercedVelocity.y,
                            minValueX = state.dismissVelocity,
                            minValueY = state.dismissVelocity
                        ) ?: getDismissDirection(
                            valueX = coercedOffset.x,
                            valueY = coercedOffset.y,
                            minValueX = state.containerWidth,
                            minValueY = state.containerHeight
                        )
                    if (dismissDirection != null) {
                        state.dismiss(dismissDirection)
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
    allowedDirections: Array<Direction>
): Velocity = copy(
    x = x.coerceWidthIn(allowedDirections, Float.MAX_VALUE),
    y = y.coerceHeightIn(allowedDirections, Float.MAX_VALUE)
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

//TODO add comments
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
