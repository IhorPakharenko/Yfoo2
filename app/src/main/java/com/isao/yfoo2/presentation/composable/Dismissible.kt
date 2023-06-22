package com.isao.yfoo2.presentation.composable

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


fun Modifier.dismissible(
    state: DismissibleState,
    directions: Set<Direction>,
    enabled: Boolean = true,
    containerWidth: Dp,
    containerHeight: Dp,
    maxRotationZ: Float = 15f,
    velocityThreshold: Dp = 125.dp,
    minHorizontalProgressThreshold: Float = 0.7f,
    minVerticalProgressThreshold: Float = 0.7f,
) = composed {
    check(minHorizontalProgressThreshold > 0f && minHorizontalProgressThreshold <= 1) {
        "minHorizontalProgressToDismiss must be greater than 0 and less than or equal to 1"
    }
    check(minVerticalProgressThreshold > 0f && minVerticalProgressThreshold <= 1) {
        "minVerticalProgressToDismiss must be greater than 0 and less than or equal to 1"
    }

    val density = LocalDensity.current
    LaunchedEffect(directions, containerWidth, containerHeight) {
        state.directions = directions
        state.maxRotationZ = maxRotationZ
        state.minHorizontalProgressThreshold = minHorizontalProgressThreshold
        state.minVerticalProgressThreshold = minVerticalProgressThreshold
        with(density) {
            state.containerWidth = containerWidth.toPx()
            state.containerHeight = containerHeight.toPx()
            state.velocityThreshold = velocityThreshold.toPx()
        }
    }

    Modifier
        .pointerInput(directions, enabled) {
            if (!enabled) return@pointerInput
            coroutineScope {
                val velocityTracker = VelocityTracker()
                detectDragGestures(
                    onDragStart = { velocityTracker.resetTracking() },
                    onDragEnd = {
                        launch {
                            state.performFling(velocityTracker.calculateVelocity())
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
                            state.performDrag(dragged)
                        }
                    })
            }
        }
        .graphicsLayer {
            translationX = state.value.x
            translationY = state.value.y
            rotationZ = state.rotationZ
        }
}
