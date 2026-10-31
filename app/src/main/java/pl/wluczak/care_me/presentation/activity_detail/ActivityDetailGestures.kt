package pl.wluczak.care_me.presentation.activity_detail

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

fun Modifier.activityDetailGestures(
    getIsDrawerOpen: () -> Boolean,
    onDrawerStateChange: (Boolean) -> Unit,
    getIsNavigatingBack: () -> Boolean,
    onNavigatingBackChange: (Boolean) -> Unit,
    onBackClick: () -> Unit
): Modifier = this.pointerInput(Unit) {
    var dragOffset = 0f
    var actionConsumedForThisDrag = false

    detectHorizontalDragGestures(
        onDragStart = {
            dragOffset = 0f
            actionConsumedForThisDrag = false
        },
        onDragEnd = {
            dragOffset = 0f
            actionConsumedForThisDrag = false
        },
        onDragCancel = {
            dragOffset = 0f
            actionConsumedForThisDrag = false
        },
        onHorizontalDrag = { change, dragAmount ->
            if (getIsNavigatingBack() || actionConsumedForThisDrag) return@detectHorizontalDragGestures

            dragOffset += dragAmount

            if (getIsDrawerOpen()) {
                if (dragOffset > 50f) {
                    onDrawerStateChange(false)
                    actionConsumedForThisDrag = true
                    change.consume()
                    dragOffset = 0f
                }
            } else {
                if (dragOffset < -50f) {
                    onDrawerStateChange(true)
                    actionConsumedForThisDrag = true
                    change.consume()
                    dragOffset = 0f
                } else if (dragOffset > 100f) {
                    onNavigatingBackChange(true)
                    actionConsumedForThisDrag = true
                    onBackClick()
                    change.consume()
                    dragOffset = 0f
                }
            }
        }
    )
}