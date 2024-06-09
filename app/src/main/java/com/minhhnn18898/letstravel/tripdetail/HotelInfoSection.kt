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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
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
fun HotelDetailBodyPager(
    hotelInfo: List<HotelInfo>,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(pageCount = {
        hotelInfo.size
    })

    HorizontalPager(
        state = pagerState,
        pageSize = defaultPageItemSize
    ) { page ->
        val pageInfo = hotelInfo[page]

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

                    HotelBasicInfo(hotelInfo = pageInfo)

                    Spacer(modifier = Modifier.height(8.dp))

                    HotelAdditionalInfo(hotelInfo = pageInfo)
                }
            }
        }
    }
}

@Composable
fun HotelBasicInfo(
    hotelInfo: HotelInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = hotelInfo.hotelName,
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        HotelStayDurationInfo(hotelInfo)
    }
}

@Composable
fun HotelStayDurationInfo(
    hotelInfo: HotelInfo,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        HotelDateInfo(
            title = "Check in",
            date = hotelInfo.checkInDate
        )

        HotelDateDivider(
            description = hotelInfo.duration
        )

        HotelDateInfo(
            title = "Check out",
            date = hotelInfo.checkOutDate
        )
    }
}

@Composable
fun HotelDateDivider(
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 24.dp)
    ) {
        Text(
            text = description,
            style = typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1
        )

        Icon(
            modifier = Modifier.size(32.dp),
            painter = painterResource(id = R.drawable.night_sight_max_24),
            contentDescription = "",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun HotelDateInfo(
    title: String,
    date: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            style = typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = date,
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun HotelAdditionalInfo(
    hotelInfo: HotelInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HotelAdditionalInfoRow(
            title = stringResource(id = R.string.address),
            description = hotelInfo.address
        )

        Spacer(modifier = Modifier.height(4.dp))

        HotelAdditionalInfoRow(
            title = stringResource(id = R.string.prices),
            description = hotelInfo.price
        )
    }
}

@Composable
fun HotelAdditionalInfoRow(
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