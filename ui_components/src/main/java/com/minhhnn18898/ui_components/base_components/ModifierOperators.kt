package com.minhhnn18898.ui_components.base_components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.drawscope.clipRect

fun Modifier.drawWithoutRect(rect: Rect?) =
    drawWithContent {
        if (rect != null) {
            clipRect(
                left = rect.left,
                top = rect.top,
                right = rect.right,
                bottom = rect.bottom,
                clipOp = ClipOp.Difference,
            ) {
                this@drawWithContent.drawContent()
            }
        } else {
            drawContent()
        }
    }