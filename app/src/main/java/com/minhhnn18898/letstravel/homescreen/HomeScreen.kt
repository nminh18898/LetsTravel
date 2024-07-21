package com.minhhnn18898.letstravel.homescreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import androidx.lifecycle.viewmodel.compose.viewModel
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.app.AppViewModelProvider
import com.minhhnn18898.letstravel.baseuicomponent.BasicLoadingView
import com.minhhnn18898.letstravel.baseuicomponent.DefaultErrorView
import com.minhhnn18898.letstravel.baseuicomponent.RoundedPolygonShape
import com.minhhnn18898.letstravel.tripinfo.ui.CreateNewTripItemDisplay
import com.minhhnn18898.letstravel.tripinfo.ui.EmptySavedTripView
import com.minhhnn18898.letstravel.tripinfo.ui.TripInfoItemDisplay
import com.minhhnn18898.letstravel.tripinfo.ui.UserTripItemDisplay
import com.minhhnn18898.letstravel.tripinfo.ui.getResult
import com.minhhnn18898.letstravel.tripinfo.ui.hasError
import com.minhhnn18898.letstravel.tripinfo.ui.hasResult
import com.minhhnn18898.letstravel.tripinfo.ui.isContentLoading
import com.minhhnn18898.letstravel.ui.theme.typography

@Composable
fun HomeScreen(
    onClickEmptyView: () -> Unit,
    onClickCreateNew: () -> Unit,
    onClickShowAllSavedTrips: () -> Unit,
    onClickTripItem: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Column(
        modifier = modifier
    ) {
        viewModel.contentState.let { contentState ->
            TripListingScreenSection(
                icon = R.drawable.your_trips_24,
                title = R.string.saved_trips,
                sectionCtaData = SectionCtaData(
                    icon = R.drawable.chevron_right_24,
                    title = R.string.show_all,
                    onClick = onClickShowAllSavedTrips
                )
            ) {
                if (contentState.isContentLoading()) {
                    BasicLoadingView(modifier)
                }

                if(contentState.hasError()) {
                    DefaultErrorView(modifier = modifier)
                }

                if (contentState.hasResult()) {
                    val items = contentState.getResult()
                    val hasUserTrip = items.any { it is UserTripItemDisplay }

                    if (hasUserTrip) {
                        ContentListTripItem(
                            modifier = modifier,
                            listUserTripItem = items,
                            onClickTripItem = onClickTripItem,
                            onClickCreateNew = onClickCreateNew
                        )
                    } else {
                        EmptySavedTripView(onClick = onClickEmptyView)
                    }
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
    LazyColumn(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(listUserTripItem) { itemDisplay ->
            
            if(itemDisplay is UserTripItemDisplay) {
                TripItemView(modifier = Modifier, itemDisplay = itemDisplay, onClick = onClickTripItem)
            }
            else if(itemDisplay is CreateNewTripItemDisplay) {
                TripItemCreateNewView(modifier = Modifier, onClick = onClickCreateNew)
            }
        }
    }
}

@Composable
private fun TripItemView(
    modifier: Modifier,
    itemDisplay: UserTripItemDisplay,
    onClick: (Long) -> Unit
) {
    val hexagon = remember {
        RoundedPolygon(
            4,
            rounding = CornerRounding(0.15f)
        )
    }
    val thumbClip = remember(hexagon) {
        RoundedPolygonShape(polygon = hexagon)
    }

    val roundCornerBgShape = RoundedCornerShape(
        topStartPercent = 0,
        topEndPercent = 50,
        bottomStartPercent = 0,
        bottomEndPercent = 50,
    )

    val thumbSize = 100
    val titleBoxMargin = thumbSize * -0.5f
    val titleTextMargin = (thumbSize * 0.5f) + 12

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(thumbSize.dp)
            .clickable {
                onClick.invoke(itemDisplay.tripId)
            }
    ) {
        val (thumbConstraint, titleConstraint) = createRefs()

        Box(
            modifier = Modifier
                .clip(thumbClip)
                .width(thumbSize.dp)
                .zIndex(2f)
                .constrainAs(thumbConstraint) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)

                    height = Dimension.fillToConstraints
                }
                .border(
                    shape = thumbClip,
                    border = BorderStroke(4.dp, MaterialTheme.colorScheme.surfaceContainerLowest)
                )
        ) {
            Image(
                painter = painterResource(itemDisplay.defaultCoverRes),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .graphicsLayer {
                        this.shape = thumbClip
                        this.clip = true
                    }
                    .size(thumbSize.dp)
            )
        }

        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.inverseOnSurface,
                    shape = roundCornerBgShape
                )
                .constrainAs(titleConstraint) {
                    centerVerticallyTo(parent)
                    start.linkTo(thumbConstraint.end, margin = titleBoxMargin.dp)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)

                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 16.dp)
                    .padding(end = 20.dp)
                    .background(
                        color = MaterialTheme.colorScheme.surfaceContainerLowest,
                        shape = roundCornerBgShape
                    )
            )

            Text(
                modifier = Modifier
                    .padding(start = titleTextMargin.dp, end = 36.dp),
                text = itemDisplay.tripName,
                style = typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TripItemCreateNewView(
    modifier: Modifier,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier
            .padding(start = 8.dp, top = 8.dp)
            .clickable {
                onClick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically) {

        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = Icons.Filled.Add,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.tertiary
        )


        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = stringResource(id = R.string.create_new_trip),
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}

@Composable
private fun TripListingScreenSection(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    modifier: Modifier = Modifier,
    sectionCtaData: SectionCtaData? = null,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement  =  Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
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

            if(sectionCtaData != null) {
                Row(
                    modifier = Modifier.clickable {
                        sectionCtaData.onClick.invoke()
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        modifier = Modifier,
                        text = stringResource(sectionCtaData.title),
                        style = typography.bodyMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                        maxLines = 1
                    )

                    Icon(
                        modifier = Modifier
                            .size(20.dp),
                        painter = painterResource(sectionCtaData.icon),
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }
            }
        }

        content()
    }
}

data class SectionCtaData (
    @DrawableRes val icon: Int,
    @StringRes val title: Int,
    val onClick: () -> Unit
)