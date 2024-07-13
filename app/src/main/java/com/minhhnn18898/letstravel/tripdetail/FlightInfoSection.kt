@file:OptIn(ExperimentalFoundationApi::class)

package com.minhhnn18898.letstravel.tripdetail

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.ui.theme.typography

private val defaultPageItemSize = object : PageSize {
    override fun Density.calculateMainAxisPageSize(
        availableSpace: Int,
        pageSpacing: Int
    ): Int {
        return (availableSpace  * 0.8f).toInt()
    }
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
        pageSize = defaultPageItemSize
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
        FlightAdditionalInfoRow(
            title = stringResource(id = R.string.flight_number),
            description = "${flightDisplayInfo.flightNumber} - ${flightDisplayInfo.operatedAirlines}"
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

        Text(
            text = airportDisplayInfo.city,
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = "(${airportDisplayInfo.airportName})",
            style = typography.bodySmall,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = time,
            style = typography.bodySmall,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}