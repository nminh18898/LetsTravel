package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.minhhnn18898.manage_trip.R

fun LazyListScope.renderExpenseTabUI(
    modifier: Modifier = Modifier
) {

    item {
        MemberList()
    }

}

@Composable
private fun MemberList(modifier: Modifier = Modifier) {

    val stroke = Stroke(width = 3f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
    )
    val color = MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .drawBehind {
                drawRoundRect(
                    color = color,
                    style = stroke,
                    cornerRadius = CornerRadius(100.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy((4).dp)
        ) {
            memberAvatarList.forEach {
                MemberItem(it)
            }
        }
    }
}

@Composable
private fun MemberItem(
    @DrawableRes drawable: Int
) {
    Image(
        painter = painterResource(drawable),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
    )
}

private val memberAvatarList = listOf(
    R.drawable.avatar_skunk,
    R.drawable.avatar_porcupine,
    R.drawable.avatar_deer,
    R.drawable.avatar_otter
)