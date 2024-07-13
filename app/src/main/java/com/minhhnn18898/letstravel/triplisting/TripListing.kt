package com.minhhnn18898.letstravel.triplisting

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.ui.theme.typography

@Composable
fun TripListingScreen(
    onClickEmptyView: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .verticalScroll(rememberScrollState())
    ) {
        TripListingScreenSection(icon = R.drawable.your_trips_24, title = R.string.your_trips) {
            EmptyTripView(onClick = onClickEmptyView)
        }
    }
}

@Composable
private fun TripListingScreenSection(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)) {
            Icon(
                painter = painterResource(icon),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(title),
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }

        content()
    }
}

@Composable
private fun EmptyTripView(
    onClick: () -> Unit
) {
    val stroke = Stroke(width = 6f,
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
    )
    val color = MaterialTheme.colorScheme.primary

    Box(
        Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 24.dp, vertical = 4.dp)
            .drawBehind {
                drawRoundRect(color = color, style = stroke, cornerRadius = CornerRadius(16.dp.toPx()))
            }.clickable {
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
                text = stringResource(R.string.create_your_first_trip),
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1
            )
        }
    }

}