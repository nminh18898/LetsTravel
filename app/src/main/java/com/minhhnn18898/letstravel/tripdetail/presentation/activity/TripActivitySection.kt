package com.minhhnn18898.letstravel.tripdetail.presentation.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
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
import com.minhhnn18898.letstravel.tripdetail.presentation.trip.TripActivityDisplayInfo
import com.minhhnn18898.ui_components.R
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView
import com.minhhnn18898.ui_components.base_components.ErrorTextView
import com.minhhnn18898.ui_components.theme.typography

@Composable
fun TripActivitySection(
    activityInfoContentState: UiState<List<TripActivityDisplayInfo>, UiState.UndefinedError>,
    onClickCreateTripActivity: () -> Unit,
    onClickActivityItem: (Long) -> Unit,
    modifier: Modifier
) {

    if(activityInfoContentState is UiState.Loading) {

    } else if(activityInfoContentState is UiState.Error) {
        ErrorTextView(
            error = stringResource(id = com.minhhnn18898.core.R.string.can_not_load_info),
            modifier = modifier
        )
    } else if(activityInfoContentState is UiState.Success) {
        val isEmpty = activityInfoContentState.data.isEmpty()

        if(isEmpty) {
            DefaultEmptyView(
                text = stringResource(id = com.minhhnn18898.letstravel.R.string.add_your_activities),
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                onClick = onClickCreateTripActivity
            )
        } else {
            ListActivity(
                listActivity = activityInfoContentState.data,
                onItemClick = onClickActivityItem
            )
        }
    }
}

@Composable
private fun ListActivity(
    listActivity: List<TripActivityDisplayInfo>,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listActivity.forEach {
            ActivityItemView(
                activityDisplayInfo = it,
                onClick = onItemClick
            )
        }
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val photoPath = activityDisplayInfo.photo

            if(photoPath.isEmpty()) {
                Image(
                    painter = painterResource(com.minhhnn18898.letstravel.R.drawable.default_activity_photo),
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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.wrapContentSize()) {

                Text(
                    text = activityDisplayInfo.date,
                    style = typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    modifier = Modifier
                        .width(60.dp)
                        .padding(vertical = 8.dp)
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = com.minhhnn18898.letstravel.R.drawable.departure_board_24),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "${activityDisplayInfo.startTime} - ${activityDisplayInfo.endTime}",
                        style = typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

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