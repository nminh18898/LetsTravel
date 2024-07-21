@file:OptIn(ExperimentalFoundationApi::class)

package com.minhhnn18898.letstravel.tripdetail

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.app.AppViewModelProvider
import com.minhhnn18898.letstravel.data.MockDataProvider
import com.minhhnn18898.letstravel.ui.theme.typography

@Composable
fun TripDetailScreen(
    modifier: Modifier = Modifier,
    viewModel: TripDetailScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
    ) {
        TripDetailHeader(modifier, headerText = "Demo Header")
        DetailSection(
            icon = R.drawable.flight_takeoff_24,
            title = R.string.flights,
            modifier = modifier) {
                FlightDetailBodyPager(
                    flightDisplayInfo = MockDataProvider.provideFlightInfo(),
                    modifier = modifier
                )
        }
        DetailSection(
            icon = R.drawable.hotel_24,
            title = R.string.hotels,
            modifier = modifier) {
                HotelDetailBodyPager(
                    hotelDisplayInfo = MockDataProvider.provideHotelInfo(),
                    modifier = modifier
                )
        }
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun TripDetailHeader(
    modifier: Modifier = Modifier,
    headerText: String = "") {
    val shape =  RoundedCornerShape(8.dp)
    val height = 180.dp
    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .background(White, shape = shape),
    ) {
        Image(
            painter = painterResource(R.drawable.default_cover_trip_detail_illus),
            contentDescription = stringResource(id = R.string.trip_detail_header_cover_content_desc),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(shape)
        )

        Text(
            text = headerText,
            style = MaterialTheme.typography.titleLarge,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onPrimaryContainer
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