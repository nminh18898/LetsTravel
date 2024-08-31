package com.minhhnn18898.tripdetail.tripinfo.presentation.triplisting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.CreateNewTripCtaDisplay
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.EmptySavedTripView
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.TripCustomCoverDisplay
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.TripDefaultCoverDisplay
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.TripInfoItemDisplay
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.UserTripDisplay
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.getResult
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.hasError
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.hasResult
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.isContentLoading
import com.minhhnn18898.ui_components.base_components.BasicLoadingView
import com.minhhnn18898.ui_components.base_components.DefaultErrorView
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes
import com.minhhnn18898.ui_components.R.drawable as CommonDrawableRes

@Composable
fun TripInfoListingScreen(
    modifier: Modifier = Modifier,
    onClickEmptyView: () -> Unit,
    onClickTripItem: (Long) -> Unit,
    onClickCreateNew: () -> Unit,
    viewModel: TripInfoListingViewModel = hiltViewModel()
) {

    Column(
        modifier = modifier.padding(vertical = 16.dp)
    ) {
        viewModel.contentState.let { contentState ->

            if (contentState.isContentLoading()) {
                BasicLoadingView(modifier)
            }

            if(contentState.hasError()) {
                DefaultErrorView(modifier = modifier)
            }

            if (contentState.hasResult()) {
                val items = contentState.getResult()
                val hasUserTrip = items.any { it is UserTripDisplay }

                if (hasUserTrip) {
                    ContentListTripItem(
                        modifier = modifier.padding(horizontal = 16.dp),
                        listUserTripItem = items,
                        onClickCreateNew = onClickCreateNew,
                        onClickTripItem = onClickTripItem
                    )
                } else {
                    EmptySavedTripView(onClick = onClickEmptyView)
                }
            }

        }
    }
}


@Composable
private fun ContentListTripItem(
    modifier: Modifier,
    listUserTripItem: List<TripInfoItemDisplay>,
    onClickTripItem: (Long) -> Unit,
    onClickCreateNew: () -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        items(listUserTripItem) { itemDisplay ->
            if (itemDisplay is UserTripDisplay) {
                TripItemView(
                    modifier = Modifier,
                    itemDisplay = itemDisplay,
                    onClick = onClickTripItem)
            } else if (itemDisplay is CreateNewTripCtaDisplay) {
                TripItemCreateNewView(
                    modifier = modifier,
                    onClick = onClickCreateNew
                )
            }
        }
    }
}

@Composable
private fun TripItemView(
    modifier: Modifier,
    itemDisplay: UserTripDisplay,
    onClick: (Long) -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .clickable {
                onClick.invoke(itemDisplay.tripId)
            },
        horizontalAlignment = Alignment.CenterHorizontally) {

        val coverDisplay = itemDisplay.coverDisplay
        if(coverDisplay is TripDefaultCoverDisplay) {
            Image(
                painter = painterResource(coverDisplay.defaultCoverRes),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(150.dp)
                    .height(100.dp)
                    .padding(top = 8.dp)
            )
        } else if(coverDisplay is TripCustomCoverDisplay) {
            AsyncImage(
                model = coverDisplay.coverPath,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                error = painterResource(id = CommonDrawableRes.empty_image_bg),
                modifier = Modifier
                    .width(150.dp)
                    .height(100.dp)
                    .padding(top = 8.dp)
            )
        }

        Text(
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp, start = 8.dp, end = 8.dp),
            text = itemDisplay.tripName,
            style = typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TripItemCreateNewView(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.inverseOnSurface)
            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
            .clickable {
                onClick.invoke()
            },
        horizontalAlignment = Alignment.CenterHorizontally) {

        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                .width(150.dp)
                .height(84.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(40.dp),
                imageVector = Icons.Filled.Add,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.tertiary
            )
        }

        Text(
            modifier = Modifier.padding(top = 12.dp, start = 8.dp, end = 8.dp),
            text = stringResource(id = CommonStringRes.create_new_trip),
            style = typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
