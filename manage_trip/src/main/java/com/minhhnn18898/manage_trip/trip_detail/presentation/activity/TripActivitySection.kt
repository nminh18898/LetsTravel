package com.minhhnn18898.manage_trip.trip_detail.presentation.activity

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.ITripActivityDisplay
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripActivityDateGroupHeader
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripActivityDisplayInfo
import com.minhhnn18898.ui_components.R
import com.minhhnn18898.ui_components.base_components.CreateNewDefaultButton
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView
import com.minhhnn18898.ui_components.base_components.ErrorTextView
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes

fun LazyListScope.renderTripActivitySection(
    activityInfoContentState: UiState<List<ITripActivityDisplay>, UiState.UndefinedError>,
    onClickCreateTripActivity: () -> Unit,
    onClickActivityItem: (Long) -> Unit,
    modifier: Modifier
) {
    if(activityInfoContentState is UiState.Error) {
        item {
            ErrorTextView(
                error = stringResource(id = com.minhhnn18898.core.R.string.can_not_load_info),
                modifier = modifier
            )
        }
    }
    else if(activityInfoContentState is UiState.Success) {
        val isEmpty = activityInfoContentState.data.isEmpty()

        if(isEmpty) {
            item {
                DefaultEmptyView(
                    text = stringResource(id = com.minhhnn18898.manage_trip.R.string.add_your_activities),
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(),
                    onClick = onClickCreateTripActivity
                )
            }

        } else {
            items(activityInfoContentState.data) { displayInfo ->
                if(displayInfo is TripActivityDateGroupHeader) {
                    ActivityDateSeparatorView(
                        title = displayInfo.title,
                        dateOrdering = displayInfo.dateOrdering,
                        resId = displayInfo.resId
                    )
                }
                else if(displayInfo is TripActivityDisplayInfo) {
                    ActivityItemView(
                        activityDisplayInfo = displayInfo,
                        onClick = onClickActivityItem
                    )
                }
            }

            if(activityInfoContentState.data.size >= 3) {
                item {
                    CreateNewDefaultButton(
                        text = stringResource(id = com.minhhnn18898.manage_trip.R.string.add_new_activity),
                        modifier = modifier.padding(start = 16.dp),
                        onClick = onClickCreateTripActivity
                    )
                }
            }
        }
    }

}

@Composable
private fun ActivityDateSeparatorView(
    title: String,
    dateOrdering: Int,
    @DrawableRes resId: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(resId),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f)
        )

        Text(
            text = "${stringResource(id = CommonStringRes.day)} $dateOrdering: $title",
            style = typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
private fun ActivityItemView(
    activityDisplayInfo: TripActivityDisplayInfo,
    onClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clickable {
                onClick(activityDisplayInfo.activityId)
            }
    ) {

        ActivityItemDescription(
            activityDisplayInfo = activityDisplayInfo,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ActivityItemFooter(
            activityDisplayInfo = activityDisplayInfo,
            modifier = Modifier.fillMaxWidth()
        )

    }
}

@Composable
private fun ActivityItemDescription(
    activityDisplayInfo: TripActivityDisplayInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val photoPath = activityDisplayInfo.photo

        if(photoPath.isEmpty()) {
            Image(
                painter = painterResource(com.minhhnn18898.manage_trip.R.drawable.default_empty_photo_trip_activity),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(132.dp)
                    .aspectRatio(1.5f)
            )
        } else {
            AsyncImage(
                model = photoPath,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(132.dp)
                    .aspectRatio(1.5f),
                placeholder = painterResource(id = R.drawable.image_placeholder),
                error = painterResource(id = R.drawable.empty_image_bg)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = activityDisplayInfo.title,
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = activityDisplayInfo.description,
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActivityItemFooter(
    activityDisplayInfo: TripActivityDisplayInfo,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        if(activityDisplayInfo.date.isNotBlankOrEmpty()) {
            ActivityScheduleInfo(
                startTime = activityDisplayInfo.startTime,
                endTime =  activityDisplayInfo.endTime
            )
        }

        if(activityDisplayInfo.price.isNotBlankOrEmpty()) {
            Text(
                text = activityDisplayInfo.price,
                style = typography.bodyLarge,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ActivityScheduleInfo(
    startTime: String,
    endTime: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = com.minhhnn18898.manage_trip.R.drawable.departure_board_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = "$startTime - $endTime",
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}