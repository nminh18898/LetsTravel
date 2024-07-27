@file:OptIn(ExperimentalFoundationApi::class)

package com.minhhnn18898.letstravel.tripdetail.ui.flight

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.baseuicomponent.CreateNewDataSimpleButton
import com.minhhnn18898.letstravel.baseuicomponent.DefaultEmptyView
import com.minhhnn18898.letstravel.tripdetail.ui.trip.AirportDisplayInfo
import com.minhhnn18898.letstravel.tripdetail.ui.trip.FlightDisplayInfo
import com.minhhnn18898.ui_components.theme.typography

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
    flightInfoContentState: UiState<List<FlightDisplayInfo>, UiState.UndefinedError>,
    onNavigateToCreateFlightInfoScreen: () -> Unit,
    modifier: Modifier
) {

    if(flightInfoContentState is UiState.Loading) {
        FlightDetailLoading()
    } else if(flightInfoContentState is UiState.Error) {
        ErrorTextView(modifier = modifier)
    } else if(flightInfoContentState is UiState.Success) {
         val isEmpty = flightInfoContentState.data.isEmpty()

        if(isEmpty) {
            DefaultEmptyView(
                text = stringResource(id = R.string.add_your_flights),
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                onClick = onNavigateToCreateFlightInfoScreen
            )
        } else {
            FlightDetailBodyPager(
                flightDisplayInfo = flightInfoContentState.data,
                modifier = modifier
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            CreateNewDataSimpleButton(
                text = stringResource(id = R.string.add_new_flight),
                modifier = modifier,
                onClick = onNavigateToCreateFlightInfoScreen
            )
        }
    }
}

@Composable
private fun ErrorTextView(modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(id = R.drawable.error_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.tertiary
        )

        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = stringResource(id = R.string.can_not_load_info),
            style = typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 3.dp
                )
            ) {
                Column(modifier = modifier.padding(
                    vertical = 8.dp,
                    horizontal = 16.dp)
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
            title = stringResource(id = R.string.flight_number),
            description = flightNumberDesc
        )

        Spacer(modifier = Modifier.height(4.dp))

        FlightAdditionalInfoRow(
            title = stringResource(id = R.string.prices),
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
            style = typography.titleLarge,
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
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}