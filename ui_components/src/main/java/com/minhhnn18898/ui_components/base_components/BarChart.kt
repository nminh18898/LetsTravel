package com.minhhnn18898.ui_components.base_components

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.ui_components.base_components.BarChartDefaults.barCornerSize
import com.minhhnn18898.ui_components.base_components.BarChartDefaults.barSpacing
import com.minhhnn18898.ui_components.base_components.BarChartDefaults.barVisualMaxThreshold
import com.minhhnn18898.ui_components.base_components.BarChartDefaults.barVisualMinThreshold
import com.minhhnn18898.ui_components.base_components.BarChartDefaults.barWidth
import com.minhhnn18898.ui_components.base_components.BarChartDefaults.groupBarAndLabelContainerHeight
import com.minhhnn18898.ui_components.base_components.BarChartDefaults.groupBarContainerHeight
import com.minhhnn18898.ui_components.theme.typography
import kotlin.math.abs

// Source:
// + https://github.com/andreivanceadev/GroupedBarGraph/tree/main
// + https://hackernoon.com/creating-a-grouped-bar-graph-using-jetpack-compose

@Suppress("ConstPropertyName")
private object BarChartDefaults {
    const val barVisualMinThreshold = 0
    const val barVisualMaxThreshold = 100

    val barWidth = 20.dp
    val barSpacing = 1.dp
    val barCornerSize = 1.dp

    val groupBarContainerHeight = barVisualMaxThreshold.dp + abs(barVisualMinThreshold).dp
    // groupBarContainerHeight + 40.dp height for the label
    val groupBarAndLabelContainerHeight = groupBarContainerHeight + 40.dp
}

private fun mapToThreshold(
    value: Float,
    sourceMin: Float,
    sourceMax: Float,
    targetMin: Float = barVisualMinThreshold.toFloat(),
    targetMax: Float = barVisualMaxThreshold.toFloat()
): Float {
    if (sourceMin == sourceMax) return targetMax

    val sourceRange = sourceMax - sourceMin
    val targetRange = targetMax - targetMin
    return targetMin + (value - sourceMin) * (targetRange / sourceRange)
}

@Composable
fun BarGraph(
    barGroups: List<BarGroup>,
    categories: List<CategoryItem>,
    onGroupSelectionChanged: (index: Int) -> Unit = {},
    backgroundColorStart: Color,
    backgroundColorEnd: Color
) {
    val backgroundBrush = Brush.verticalGradient(
        listOf(backgroundColorStart, backgroundColorEnd)
    )

    Column(
        modifier = Modifier
            .background(
                brush = backgroundBrush,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        BarGroupContent(barGroups = barGroups, onGroupSelectionChanged)

        if(categories.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            BarGroupCategories(categories)
        }
    }

}

@Composable
private fun BarGroupContent(
    barGroups: List<BarGroup>,
    onGroupSelectionChanged: (index: Int) -> Unit = {},
) {
    val selectedGroupIndex = remember { mutableIntStateOf(-1) }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .fillMaxWidth()
            .padding(8.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        val values = barGroups.flatMap { barGroup -> barGroup.values.map { it.value } }
        val minValue = values.minOrNull() ?: 0
        val maxValue = values.maxOrNull() ?: 0

        barGroups.forEachIndexed { index, item ->
            if (index == 0) {
                Spacer(modifier = Modifier.weight(1f))
            }
            ChartBarGroup(
                label = item.label,
                values = item.values,
                valueRange = IntRange(minValue, maxValue),
                onGroupSelected = {
                    selectedGroupIndex.intValue = index
                    onGroupSelectionChanged(selectedGroupIndex.intValue)
                },
                onRemoveSelection = {
                    selectedGroupIndex.intValue = -1
                    onGroupSelectionChanged(selectedGroupIndex.intValue)
                },
                isSelected = selectedGroupIndex.intValue == index,
                isNothingSelected = selectedGroupIndex.intValue == -1
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun BarGroupCategories(categories: List<CategoryItem>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        categories.forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(12.dp)
                        .background(item.itemColor))

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = item.description,
                    style = typography.labelSmall,
                    color = item.descriptionColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChartBarGroup(
    modifier: Modifier = Modifier,
    label: String,
    values: List<BarItem>,
    valueRange: IntRange,
    onGroupSelected: () -> Unit = {},
    onRemoveSelection: () -> Unit = {},
    isSelected: Boolean,
    isNothingSelected: Boolean
) {
    Column(
        modifier = modifier
            .height(groupBarAndLabelContainerHeight)
            .pointerInteropFilter { event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        onGroupSelected()
                    }

                    MotionEvent.ACTION_UP -> {
                        onRemoveSelection()
                    }

                    MotionEvent.ACTION_CANCEL -> {
                        onRemoveSelection()
                    }
                }
                true
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GroupLabel(
            text = label,
            isHighlighted = isSelected
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.height(groupBarContainerHeight), verticalAlignment = Alignment.Bottom
        ) {
            values.forEachIndexed { index, item ->
                val realPercentage = mapToThreshold(
                    value = item.value.toFloat(),
                    sourceMin = 0f,
                    sourceMax =  valueRange.last.toFloat()
                ).toInt()

                val color = item.color
                val yOffset: Int
                val percentage = realPercentage.coerceAtLeast(1)

                yOffset = if (percentage >= 0) {
                    abs(barVisualMinThreshold)
                } else if (percentage in barVisualMinThreshold downTo -1) {
                    abs(barVisualMinThreshold) + percentage
                } else {
                    0
                }
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    ChartBar(
                        percentage = percentage,
                        brush = Brush.verticalGradient(listOf(color, color)),
                        isHighlighted = isSelected || isNothingSelected,
                        description = item.description,
                        descriptionColor = item.descriptionColor
                    )
                    Spacer(modifier = Modifier.height(yOffset.dp))
                }
                if (index in 0 until values.size - 1) {
                    Spacer(modifier = Modifier.width(barSpacing))
                }
            }
        }
    }
}


@Composable
private fun GroupLabel(
    text: String,
    textStyle: TextStyle = MaterialTheme.typography.labelMedium,
    isHighlighted: Boolean = false
) {
    Text(
        modifier = Modifier.padding(bottom = 8.dp),
        text = text,
        color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        style = textStyle
    )
}

@Composable
fun ChartBar(
    percentage: Int,
    brush: Brush,
    description: String,
    descriptionColor: Color,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .background(Color.Yellow)
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(barCornerSize))
                .height(abs(percentage).dp)
                .width(barWidth)
                .background(brush)
                .background(color = if (!isHighlighted) Color.Black.copy(alpha = 0.5f) else Color.Transparent),
        )

        if(description.isNotBlankOrEmpty()) {
            DescriptionText(description, descriptionColor)
        }
    }
}

@Composable
fun DescriptionText(
    description: String,
    descriptionColor: Color = Color.Unspecified
) {
    val textMeasurer = rememberTextMeasurer()
    val textColor = if(descriptionColor == Color.Unspecified) MaterialTheme.colorScheme.primary else descriptionColor

    Canvas(modifier = Modifier
        .offset(x = (-8).dp, y = (-4).dp)
    ) {
        rotate(-90f) {
            val measuredText =
                textMeasurer.measure(
                    AnnotatedString(description),
                    constraints = Constraints.fixed(
                        width = 100.dp.roundToPx(),
                        height = 12.dp.roundToPx()
                    ),
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = textColor
                    )
                )

            drawText(textLayoutResult = measuredText)
        }
    }
}

data class BarGroup(
    val label: String,
    val values: List<BarItem>
)

data class BarItem(
    val value: Int,
    val color: Color,
    val description: String = "",
    val descriptionColor: Color = Color.Unspecified
)

data class CategoryItem(
    val itemColor: Color,
    val description: String,
    val descriptionColor: Color
)