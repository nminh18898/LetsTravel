package com.minhhnn18898.ui_components.base_components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.minhhnn18898.core.utils.formatWithCommas
import com.minhhnn18898.ui_components.theme.typography
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.roundToInt

private const val chartDegrees = 360f
private const val emptyIndex = -1

// Credit: https://github.com/giorgospat/compose-charts/tree/main

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: PieChartData,
    textColor: Color = MaterialTheme.colorScheme.primary
) {

    val inputValues = data.items.map { it.value }
    val colors = data.items.map { it.color }

    assert(inputValues.isNotEmpty() && inputValues.size == colors.size) {
        "Input values count must be equal to colors size"
    }

    // start drawing clockwise (top to right)
    var startAngle = 270f

    // calculate each input percentage
    val proportions = inputValues.toPercent()

    // calculate each input slice degrees
    val angleProgress = proportions.map { prop ->
        chartDegrees * prop / 100
    }

    // clicked slice in chart
    var clickedItemIndex by remember { mutableIntStateOf(emptyIndex) }

    // calculate each slice end point in degrees, for handling click position
    val progressSize = mutableListOf<Float>()

    LaunchedEffect(angleProgress) {
        progressSize.add(angleProgress.first())
        for (x in 1 until angleProgress.size) {
            progressSize.add(angleProgress[x] + progressSize[x - 1])
        }
    }

    // Title text config (text is centered in the chart)
    val density = LocalDensity.current
    val textFontSize = with(density) { 16.dp.toPx() }
    val textPaint = remember {
        Paint().apply {
            color = textColor.toArgb()
            textSize = textFontSize
            textAlign = Paint.Align.CENTER
            setTypeface(Typeface.DEFAULT_BOLD)
        }
    }

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {

        val canvasSize = min(constraints.maxWidth, constraints.maxHeight)
        val size = Size(canvasSize.toFloat(), canvasSize.toFloat())
        val canvasSizeDp = with(density) { canvasSize.toDp() }

        Canvas(
            modifier = Modifier
                .size(canvasSizeDp)
                .pointerInput(inputValues) {

                    detectTapGestures { offset ->
                        val clickedAngle = touchPointToAngle(
                            width = canvasSize.toFloat(),
                            height = canvasSize.toFloat(),
                            touchX = offset.x,
                            touchY = offset.y,
                            chartDegrees = chartDegrees
                        )
                        progressSize.forEachIndexed { index, item ->
                            if (clickedAngle <= item) {
                                clickedItemIndex = index
                                return@detectTapGestures
                            }
                        }
                    }
                }
        ) {
            val sliceWidth = 24.dp

            angleProgress.forEachIndexed { index, angle ->
                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = angle,
                    useCenter = false,
                    size = size,
                    style = Stroke(width = sliceWidth.toPx())
                )
                startAngle += angle
            }

            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    data.title,
                    (canvasSize / 2) + textFontSize / 4,
                    (canvasSize / 2) + textFontSize / 4,
                    textPaint
                )
            }
        }
    }
}

data class PieChartData(
    val items: List<PieChartItem>,
    val title: String = ""
)

data class PieChartItem(
    val color: Color,
    val label: String,
    val value: Float,
    val percent: Float,
)

internal fun touchPointToAngle(
    width: Float,
    height: Float,
    touchX: Float,
    touchY: Float,
    chartDegrees: Float
): Double {
    val x = touchX - (width * 0.5f)
    val y = touchY - (height * 0.5f)
    var angle = Math.toDegrees(atan2(y.toDouble(), x.toDouble()) + Math.PI / 2)
    angle = if (angle < 0) angle + chartDegrees else angle
    return angle
}

internal fun List<Float>.toPercent(): List<Float> {
    return this.map { item ->
        item * 100 / this.sum()
    }
}

@Composable
fun PieChartWithLabel(
    chartSize: Dp,
    data: PieChartData,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PieChart(
            data = data,
            modifier =  Modifier.size(chartSize),
            textColor = textColor
        )
        
        Spacer(modifier = Modifier.width(40.dp))
        
        PieChartLabelColumn(items = data.items)
    }

}

@Composable
fun PieChartLabelColumn(
    items: List<PieChartItem>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { portion ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(16.dp)
                        .background(portion.color))

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "${portion.label} - ${portion.value.toLong().formatWithCommas()} (${portion.percent.roundToInt()}%)",
                    style = typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
