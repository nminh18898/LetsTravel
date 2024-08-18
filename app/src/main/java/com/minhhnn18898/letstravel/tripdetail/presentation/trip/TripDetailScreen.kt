package com.minhhnn18898.letstravel.tripdetail.presentation.trip

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import coil.compose.AsyncImage
import com.minhhnn18898.app_navigation.appbarstate.AppBarActionsState
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.tripdetail.presentation.flight.FlightDetailBody
import com.minhhnn18898.letstravel.tripdetail.presentation.hotel.HotelDetailBody
import com.minhhnn18898.letstravel.tripinfo.presentation.base.TripCustomCoverDisplay
import com.minhhnn18898.letstravel.tripinfo.presentation.base.TripDefaultCoverDisplay
import com.minhhnn18898.letstravel.tripinfo.presentation.base.UserTripDisplay
import com.minhhnn18898.ui_components.base_components.DefaultErrorView
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes
import com.minhhnn18898.ui_components.R.drawable as CommonDrawableRes

@Composable
fun TripDetailScreen(
    onComposedTopBarActions: (AppBarActionsState) -> Unit,
    navigateUp: () -> Unit,
    onNavigateToEditFlightInfoScreen: (Long, Long) -> Unit,
    onNavigateEditHotelScreen: (Long, Long) -> Unit,
    onNavigateToEditTripScreen: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TripDetailScreenViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        onComposedTopBarActions(
            AppBarActionsState(
                actions = {
                    IconButton(
                        onClick = {
                            onNavigateToEditTripScreen.invoke(viewModel.tripId)
                        }
                    ) {
                        Icon(
                            painter = painterResource(CommonDrawableRes.edit_square_24),
                            contentDescription = "",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        )
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventTriggerer.collect { event ->
                if(event == TripDetailScreenViewModel.Event.CloseScreen) {
                    navigateUp.invoke()
                }
            }
        }
    }

    Column(
        Modifier
            .verticalScroll(rememberScrollState())
    ) {
        TripDetailHeader(modifier, viewModel.tripInfoContentState)

        Spacer(modifier = Modifier.height(16.dp))
        
        EstimatedBudgetSection(
            modifier = modifier,
            estimateBudget = viewModel.estimateBudgetDisplay
        )

        // Flight Info
        DetailSection(
            icon = R.drawable.flight_takeoff_24,
            title = CommonStringRes.flights,
            modifier = modifier) {

            FlightDetailBody(
                viewModel.flightInfoContentState,
                modifier = modifier,
                onClickCreateNewFlight = {
                    onNavigateToEditFlightInfoScreen.invoke(viewModel.tripId, 0L)
                },
                onClickFlightInfoItem = { flightId ->
                    onNavigateToEditFlightInfoScreen.invoke(viewModel.tripId, flightId)
                }
            )
        }

        // Hotel info
        DetailSection(
            icon = R.drawable.hotel_24,
            title = CommonStringRes.hotels,
            modifier = modifier) {
                HotelDetailBody(
                    hotelInfoContentState = viewModel.hotelInfoContentState,
                    modifier = modifier,
                    onClickCreateHotelInfo = {
                        onNavigateEditHotelScreen.invoke(viewModel.tripId, 0L)
                    },
                    onClickHotelInfoItem = { hotelId ->
                        onNavigateEditHotelScreen.invoke(viewModel.tripId, hotelId)
                    }
                )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TripDetailHeader(
    modifier: Modifier = Modifier,
    tripInfoState: UiState<UserTripDisplay, UiState.UndefinedError>) {

    if(tripInfoState is UiState.Loading) {
        TripDetailHeaderLoading()
    }

    if(tripInfoState is UiState.Error) {
        DefaultErrorView(modifier = modifier)
    }

    if(tripInfoState is UiState.Success) {
        TripDetailHeaderContent(tripInfoItemDisplay = tripInfoState.data, modifier = modifier)
    }
}

@Composable
private fun EstimatedBudgetSection(
    modifier: Modifier = Modifier,
    estimateBudget: String) {

   if(estimateBudget.isNotBlankOrEmpty()) {
       Row(
           modifier = modifier.padding(horizontal = 16.dp),
           verticalAlignment = Alignment.CenterVertically
       ) {
           Icon(
               painter = painterResource(R.drawable.price_change_24),
               contentDescription = "",
               tint = MaterialTheme.colorScheme.primary
           )

           Spacer(modifier = Modifier.width(8.dp))

           Text(
               text = "${stringResource(CommonStringRes.estimated_budget)}:",
               style = typography.titleMedium,
               color = MaterialTheme.colorScheme.primary,
               maxLines = 1
           )

           Spacer(modifier = Modifier.width(8.dp))

           Text(
               text = estimateBudget,
               style = typography.titleMedium,
               color = MaterialTheme.colorScheme.tertiary,
               maxLines = 1
           )
       }
   }
}

@Composable
private fun TripDetailHeaderLoading() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        // `infiniteRepeatable` repeats the specified duration-based `AnimationSpec` infinitely.
        animationSpec = infiniteRepeatable(
            // The `keyframes` animates the value by specifying multiple timestamps.
            animation = keyframes {
                // One iteration is 1000 milliseconds.
                durationMillis = 1000
                // 0.7f at the middle of an iteration.
                0.7f at 500
            },
            // When the value finishes animating from 0f to 1f, it repeats by reversing the
            // animation direction.
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )


    Box(
        modifier = Modifier
            .height(180.dp)
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = alpha))
    )
}

@Composable
private fun TripDetailHeaderContent(
    tripInfoItemDisplay: UserTripDisplay,
    modifier: Modifier = Modifier) {

    val shape =  RoundedCornerShape(8.dp)
    val height = 180.dp

    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .background(White, shape = shape),
    ) {

        val coverDisplay = tripInfoItemDisplay.coverDisplay
        if(coverDisplay is TripDefaultCoverDisplay) {
            Image(
                painter = painterResource(coverDisplay.defaultCoverRes),
                contentDescription = stringResource(id = CommonStringRes.trip_detail_header_cover_content_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
        } else if(coverDisplay is TripCustomCoverDisplay) {
            AsyncImage(
                model = coverDisplay.coverPath,
                contentDescription = stringResource(id = CommonStringRes.trip_detail_header_cover_content_desc),
                contentScale = ContentScale.Crop,
                error = painterResource(id =CommonDrawableRes.empty_image_bg),
                modifier = Modifier
                    .fillMaxSize()
            )
        }

        Text(
            text = tripInfoItemDisplay.tripName,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Bold,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerLowest.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(horizontal = 24.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DetailSection(
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