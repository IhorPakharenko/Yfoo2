package com.isao.yfoo2.ui

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs


//TODO support rtl
fun Modifier.dismissible(
    state: DismissibleState,
    directions: Array<Direction> = Direction.values(), //TODO replace with immutable list
    canDismiss: (Offset, Float, Float) -> Boolean = {
        abs(it.x) > state.containerWidth / 4 || abs(it.y) > state.containerHeight / 4
    },
    maxRotationZ: Int = 0,
    onDismiss: (Direction) -> Unit,
    onDismissCancel: () -> Unit
) = pointerInput(Unit) {
    coroutineScope {
        detectDragGestures(
            onDragStart = {},
            onDragEnd = {
                launch {
                    val coercedOffset = state.offset.targetValue.coerceIn(
                        allowedDirections = directions,
                        maxWidth = state.containerWidth,
                        maxHeight = state.containerHeight
                    )
                    if (canDismiss(coercedOffset)) {
                        onDismissCancel()
                        state.reset()
                    } else {

                    }
                }
            },
            onDragCancel = {
                launch {
                    onDismissCancel()
                    state.reset()
                }
            },
            onDrag = { change: PointerInputChange, dragged: Offset ->
                launch {
                    change.consume()
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

}

private fun Offset.coerceIn(
    allowedDirections: Array<Direction>,
    maxWidth: Float,
    maxHeight: Float,
): Offset = copy(
    x = x.coerceIn(
        if (allowedDirections.contains(Direction.Start)) -maxWidth else 0f,
        if (allowedDirections.contains(Direction.End)) maxWidth else 0f
    ),
    y = y.coerceIn(
        if (allowedDirections.contains(Direction.Up)) -maxHeight else 0f,
        if (allowedDirections.contains(Direction.Down)) maxHeight else 0f,
    )
)
