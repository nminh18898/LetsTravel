package com.minhhnn18898.letstravel.baseuicomponent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.ui_components.theme.typography

@Composable
fun DefaultEmptyView(
    text: String,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val stroke = Stroke(width = 6f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
    )
    val color = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .drawBehind {
                drawRoundRect(color = color, style = stroke, cornerRadius = CornerRadius(16.dp.toPx()))
            }
            .clickable {
                onClick.invoke()
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.edit_note_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = text,
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }
    }
}