package com.minhhnn18898.letstravel.homescreen

import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.minhhnn18898.discover.presentation.discover.DiscoverScreen
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.manage_trip.trip_info.presentation.base.CreateNewTripCtaDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.EmptySavedTripView
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentResult
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripCustomCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripDefaultCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripInfoItemDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.UserTripDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.getResult
import com.minhhnn18898.manage_trip.trip_info.presentation.base.hasError
import com.minhhnn18898.manage_trip.trip_info.presentation.base.hasResult
import com.minhhnn18898.manage_trip.trip_info.presentation.base.isContentLoading
import com.minhhnn18898.ui_components.base_components.CreateNewDefaultButton
import com.minhhnn18898.ui_components.base_components.HexagonShape
import com.minhhnn18898.ui_components.base_components.SectionCtaData
import com.minhhnn18898.ui_components.error_view.DefaultErrorView
import com.minhhnn18898.ui_components.loading_view.BasicLoadingView
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes
import com.minhhnn18898.ui_components.R.drawable as CommonDrawableRes

@Composable
fun HomeScreen(
    onClickEmptyView: () -> Unit,
    onClickCreateNew: () -> Unit,
    onClickShowAllSavedTrips: () -> Unit,
    onClickTripItem: (Long) -> Unit,
    onNavigateToSignInScreen: () -> Unit,
    onNavigateToArticleDetailScreen: (id: String, position: Int, listArticles: List<Pair<String, String>>) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel()
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        val contentState by viewModel.contentState.collectAsStateWithLifecycle()
        val hasTrip = (contentState as? GetSavedTripInfoContentResult)?.listTripItem?.isNotEmpty() ?: false

        TripListingScreenSection(
            icon = R.drawable.your_trips_24,
            title = CommonStringRes.saved_trips,
            sectionCtaData =
                if (hasTrip)
                    SectionCtaData(
                        icon = CommonDrawableRes.chevron_right_24,
                        title = CommonStringRes.show_all,
                        onClick = onClickShowAllSavedTrips
                    )
                else null
        ) {
            if (contentState.isContentLoading()) {
                BasicLoadingView(modifier)
            }

            if (contentState.hasError()) {
                DefaultErrorView(modifier = modifier)
            }

            if (contentState.hasResult()) {
                val items = contentState.getResult()

                if (items.isNotEmpty()) {
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

        TripListingScreenSection(
            icon = R.drawable.travel_explore_24,
            title = CommonStringRes.explore,
            modifier = modifier
        ) {
            DiscoverScreen(
                onNavigateToSignInScreen = onNavigateToSignInScreen,
                onNavigateToArticlesDetailScreen = onNavigateToArticleDetailScreen,
                modifier = modifier
            )
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
    Column(
        modifier = modifier.padding(start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        listUserTripItem.forEach { itemDisplay ->
            if(itemDisplay is UserTripDisplay) {
                TripItemView(modifier = Modifier, itemDisplay = itemDisplay, onClick = onClickTripItem)
            }
            else if(itemDisplay is CreateNewTripCtaDisplay) {
                TripItemCreateNewView(modifier = Modifier, onClick = onClickCreateNew)
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
    val thumbClip = HexagonShape()

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
            val coverDisplay = itemDisplay.coverDisplay
            if(coverDisplay is TripDefaultCoverDisplay) {
                Image(
                    painter = painterResource(coverDisplay.defaultCoverRes),
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
            else if(coverDisplay is TripCustomCoverDisplay) {
                AsyncImage(
                    model = Uri.parse(coverDisplay.coverPath),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = CommonDrawableRes.empty_image_bg),
                    placeholder = painterResource(id = CommonDrawableRes.image_placeholder),
                    modifier = Modifier
                        .graphicsLayer {
                            this.shape = thumbClip
                            this.clip = true
                        }
                        .size(thumbSize.dp)
                )
            }
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
    CreateNewDefaultButton(
        text = stringResource(id = CommonStringRes.create_new_trip),
        modifier = modifier.padding(start = 8.dp, top = 8.dp),
        onClick = onClick
    )
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