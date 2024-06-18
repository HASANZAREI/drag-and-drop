package com.example.dragitem

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

internal val LocalDragTargetState = compositionLocalOf { DragAndDrop() }

@Composable
fun <T> DragTarget(
    modifier: Modifier = Modifier,
    dataToDrop: T,
    viewModel: MainViewModel,
    content: @Composable () -> Unit
) {
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var currentState = LocalDragTargetState.current

    Box(
        modifier = Modifier
            .onGloballyPositioned {
                currentPosition = it.localToWindow(Offset.Zero)
            }
            .pointerInput(Unit) {
                detectDragGesturesAfterLongPress(
                    onDragStart = {
                        viewModel.startDragging()
                        currentState.dataToDrag = dataToDrop
                        currentState.isDragging = true
                        currentState.dragPosition = currentPosition + it
                        currentState.draggableComposable = content
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentState.dragOffset += Offset(dragAmount.x, dragAmount.y)
                    },
                    onDragEnd = {
                        viewModel.stopDragging()
                        currentState.isDragging = false
                        currentState.dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        viewModel.stopDragging()
                        currentState.isDragging = false
                        currentState.dragOffset = Offset.Zero
                    }
                )
            }
    ) {
        content()
    }
}

@Composable
fun <T> DropItem(
    modifier: Modifier,
    content: @Composable() (BoxScope.(isInBounds: Boolean, data: T?) -> Unit)
) {
    val dragInfo = LocalDragTargetState.current
    val dragPosition = dragInfo.dragPosition
    val dragOffset = dragInfo.dragOffset
    var isCurrentDropTarget by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .onGloballyPositioned {
                it.boundsInWindow().let { rect ->
                    isCurrentDropTarget = rect.contains(dragPosition + dragOffset)
                }
            }
    ) {
        val data =
            if (isCurrentDropTarget && !dragInfo.isDragging) dragInfo.dataToDrag as T? else null
        content(isCurrentDropTarget, data)
    }
}

@Composable
fun DraggableScreen(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val state = remember {
        DragAndDrop()
    }
    CompositionLocalProvider(
        LocalDragTargetState provides state
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            content()
            if (state.isDragging) {
                var targetSize by remember {
                    mutableStateOf(IntSize.Zero)
                }
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            val offset = (state.dragPosition + state.dragOffset)
                            scaleX = 1.3f
                            scaleY = 1.3f
                            alpha = if (targetSize == IntSize.Zero) 0f else .9f
                            translationX = offset.x.minus(targetSize.width / 2)
                            translationY = offset.y.minus(targetSize.height / 2)
                        }
                        .onGloballyPositioned { targetSize = it.size }
                ) {
                    state.draggableComposable?.invoke()
                }
            }
        }
    }
}

internal class DragAndDrop {
    var isDragging: Boolean by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var dragOffset by mutableStateOf(Offset.Zero)
    var draggableComposable by mutableStateOf<(@Composable () -> Unit)?>(null)
    var dataToDrag by mutableStateOf<Any?>(null)
}