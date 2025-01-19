package com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.flight

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.AirportDisplayInfo
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.FlightDisplayInfo
import com.minhhnn18898.ui_components.base_components.CreateNewDefaultButton
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView
import com.minhhnn18898.ui_components.error_view.ErrorTextView
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.string as CommonStringRes

private val defaultPageItemSize = object : PageSize {
    override fun Density.calculateMainAxisPageSize(
        availableSpace: Int,
        pageSpacing: Int
    ): Int {
        return (availableSpace  * 0.8f).toInt()
    }
}

@Composable
fun FlightDetailBody(
    flightInfoContentState: UiState<List<FlightDisplayInfo>>,
    onClickCreateNewFlight: () -> Unit,
    onClickFlightInfoItem: (Long) -> Unit,
    modifier: Modifier
) {

    when (flightInfoContentState) {
        is UiState.Loading -> {
            FlightDetailLoading()
        }

        is UiState.Error -> {
            ErrorTextView(
                error = stringResource(id = CommonStringRes.can_not_load_info),
                modifier = modifier
            )
        }

        is UiState.Success -> {
            FlightDetailContent(
                flightInfo = flightInfoContentState.data,
                onClickCreateNewFlight = onClickCreateNewFlight,
                onClickFlightInfoItem = onClickFlightInfoItem,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun FlightDetailContent(
    flightInfo: List<FlightDisplayInfo>,
    onClickCreateNewFlight: () -> Unit,
    onClickFlightInfoItem: (Long) -> Unit,
    modifier: Modifier
) {
    if (flightInfo.isEmpty()) {
        DefaultEmptyView(
            text = stringResource(id = R.string.add_your_flights),
            modifier = Modifier
                .height(100.dp)
                .fillMaxWidth(),
            onClick = onClickCreateNewFlight
        )
    } else {
        FlightDetailBodyPager(
            flightDisplayInfo = flightInfo,
            onClickFlightInfoItem = onClickFlightInfoItem,
            modifier = modifier
        )

        Spacer(modifier = Modifier.height(8.dp))

        CreateNewDefaultButton(
            text = stringResource(id = CommonStringRes.add_new_flight),
            modifier = modifier.padding(start = 16.dp, top = 8.dp),
            onClick = onClickCreateNewFlight
        )
    }
}

@Composable
private fun FlightDetailLoading() {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite loading")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000
                0.7f at 500
            },
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Row(modifier = Modifier.padding(horizontal = 16.dp)) {
        FlightLoadingSkeletonItem(alpha)

        Spacer(modifier = Modifier.width(60.dp))

        FlightLoadingSkeletonItem(alpha)
    }
}

@Composable
private fun FlightLoadingSkeletonItem(alpha: Float) {
    val configuration = LocalConfiguration.current
    Box(
        modifier = Modifier
            .height(128.dp)
            .width(configuration.screenWidthDp.dp * 0.6f)
            .background(
                color = Color.LightGray.copy(alpha = alpha),
                shape = RoundedCornerShape(12.dp)
            )
    )
}

@Composable
fun FlightDetailBodyPager(
    flightDisplayInfo: List<FlightDisplayInfo>,
    onClickFlightInfoItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(pageCount = {
        flightDisplayInfo.size
    })

    HorizontalPager(
        state = pagerState,
        pageSize = defaultPageItemSize,
        verticalAlignment = Alignment.CenterVertically
    ) { page ->
        val pageInfo = flightDisplayInfo[page]

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)

        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.clickable {
                    onClickFlightInfoItem(pageInfo.flightId)
                }
            ) {
                Column(
                    modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                ) {

                    FlightBasicInfoRow(flightDisplayInfo = pageInfo)

                    Spacer(modifier = Modifier.height(8.dp))

                    FlightAdditionalInfo(flightDisplayInfo = pageInfo)
                }
            }
        }
    }
}

@Composable
fun FlightAdditionalInfo(
    flightDisplayInfo: FlightDisplayInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        var flightNumberDesc = flightDisplayInfo.flightNumber
        if(flightDisplayInfo.operatedAirlines.isNotEmpty()) {
            flightNumberDesc = "$flightNumberDesc - ${flightDisplayInfo.operatedAirlines}"
        }
        FlightAdditionalInfoRow(
            title = stringResource(id = CommonStringRes.flight_number),
            description = flightNumberDesc
        )

        Spacer(modifier = Modifier.height(4.dp))

        FlightAdditionalInfoRow(
            title = stringResource(id = CommonStringRes.prices),
            description = flightDisplayInfo.price
        )
    }
}

@Composable
fun FlightAdditionalInfoRow(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$title:",
            style = typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = modifier.width(4.dp))

        Text(
            text = description,
            style = typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun FlightBasicInfoRow(
    flightDisplayInfo: FlightDisplayInfo,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        FlightDestinationInfoRow(
            airportDisplayInfo = flightDisplayInfo.departAirport,
            time = flightDisplayInfo.departureTime
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(
                text = flightDisplayInfo.duration,
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                maxLines = 1
            )

            Icon(
                modifier = Modifier.size(32.dp),
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        FlightDestinationInfoRow(
            airportDisplayInfo = flightDisplayInfo.destinationAirport,
            time = flightDisplayInfo.arrivalTime
        )
    }
}

@Composable
fun FlightDestinationInfoRow(
    airportDisplayInfo: AirportDisplayInfo,
    time: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = airportDisplayInfo.code,
            style = typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if(airportDisplayInfo.city.isNotEmpty()) {
            Text(
                text = airportDisplayInfo.city,
                style = typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if(airportDisplayInfo.airportName.isNotEmpty()) {
            Text(
                text = "(${airportDisplayInfo.airportName})",
                style = typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = time,
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}