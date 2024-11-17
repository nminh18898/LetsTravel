package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.ui_components.theme.typography

fun LazyListScope.renderExpenseTabUI(
    modifier: Modifier = Modifier
) {

    item {
        MemberList()
    }

    item {
        BillInfo()
    }
}

@Composable
private fun BillInfo(modifier: Modifier = Modifier) {
    val stroke = Stroke(
        width = 2f
    )
    val color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .drawBehind {
                drawRoundRect(
                    color = color,
                    style = stroke,
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
            .padding(12.dp)
    ) {
        Column {
            BillHeader()
            Spacer(modifier = Modifier.height(8.dp))
            BillDescription()
        }
    }
}

@Composable
private fun BillHeader(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(3f)
                .padding(end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(memberAvatarList.first()),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Dinner at boat restaurant",
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "$35.22",
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$11.74/person",
                style = typography.labelMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun BillDescription(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier
            .weight(2f)
            .padding(end = 8.dp)
        ) {
            Text(
                text = "Short description",
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                overflow = TextOverflow.Ellipsis,
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "17/11/2024 - 10:52",
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            memberAvatarList.forEach {
                MemberItem(it, 24.dp)
            }
        }
    }
}

@Composable
private fun MemberList(modifier: Modifier = Modifier) {

    val stroke = Stroke(
        width = 3f,
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
                MemberItem(it, 48.dp)
            }
        }
    }
}

@Composable
private fun MemberItem(
    @DrawableRes drawable: Int,
    itemSize: Dp,
) {
    Image(
        painter = painterResource(drawable),
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(itemSize)
            .clip(CircleShape)
    )
}

private val memberAvatarList = listOf(
    R.drawable.avatar_skunk,
    R.drawable.avatar_porcupine,
    R.drawable.avatar_deer,
    R.drawable.avatar_otter
)