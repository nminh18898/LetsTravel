@file:OptIn(ExperimentalFoundationApi::class)

package com.minhhnn18898.manage_trip.trip_detail.presentation.hotel

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.HotelDisplayInfo
import com.minhhnn18898.ui_components.base_components.CreateNewDefaultButton
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView
import com.minhhnn18898.ui_components.base_components.ErrorTextView
import com.minhhnn18898.ui_components.theme.typography
import com.minhhnn18898.core.R.plurals as CommonStringPluralsRes
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
fun HotelDetailBody(
    hotelInfoContentState: UiState<List<HotelDisplayInfo>, UiState.UndefinedError>,
    onClickCreateHotelInfo: () -> Unit,
    onClickHotelInfoItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    if(hotelInfoContentState is UiState.Loading) {
        HotelDetailLoading()
    } else if(hotelInfoContentState is UiState.Error) {
        ErrorTextView(
            error = stringResource(id = CommonStringRes.can_not_load_info),
            modifier = modifier
        )
    } else if(hotelInfoContentState is UiState.Success) {
        val isEmpty = hotelInfoContentState.data.isEmpty()

        if(isEmpty) {
            DefaultEmptyView(
                text = stringResource(id = R.string.add_your_hotels),
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(),
                onClick = onClickCreateHotelInfo
            )
        } else {
            HotelDetailBodyPager(
                hotelDisplayInfo = hotelInfoContentState.data,
                onClickHotelInfoItem = onClickHotelInfoItem,
                modifier = modifier
            )

            Spacer(modifier = Modifier.height(8.dp))

            CreateNewDefaultButton(
                text = stringResource(id = CommonStringRes.add_new_hotel),
                modifier = modifier.padding(start = 16.dp, top = 8.dp),
                onClick = onClickCreateHotelInfo
            )
        }
    }
}

@Composable
private fun HotelDetailLoading() {
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
        HotelLoadingSkeletonItem(alpha)

        Spacer(modifier = Modifier.width(60.dp))

        HotelLoadingSkeletonItem(alpha)
    }
}

@Composable
private fun HotelLoadingSkeletonItem(alpha: Float) {
    val configuration = LocalConfiguration.current
    Box(
        modifier = Modifier
            .height(136.dp)
            .width(configuration.screenWidthDp.dp * 0.6f)
            .background(
                color = Color.LightGray.copy(alpha = alpha),
                shape = RoundedCornerShape(12.dp)
            )
    )
}

@Composable
fun HotelDetailBodyPager(
    hotelDisplayInfo: List<HotelDisplayInfo>,
    onClickHotelInfoItem: (Long) -> Unit,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState(pageCount = {
        hotelDisplayInfo.size
    })

    HorizontalPager(
        state = pagerState,
        pageSize = defaultPageItemSize
    ) { page ->
        val pageInfo = hotelDisplayInfo[page]

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Card(
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.clickable {
                    onClickHotelInfoItem.invoke(pageInfo.hotelId)
                }
            ) {
                Column(
                    modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                ) {

                    HotelBasicInfo(hotelDisplayInfo = pageInfo)

                    Spacer(modifier = Modifier.height(8.dp))

                    HotelAdditionalInfo(hotelDisplayInfo = pageInfo)
                }
            }
        }
    }
}

@Composable
fun HotelBasicInfo(
    hotelDisplayInfo: HotelDisplayInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = hotelDisplayInfo.hotelName,
            style = typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        HotelStayDurationInfo(hotelDisplayInfo)
    }
}

@Composable
fun HotelStayDurationInfo(
    hotelDisplayInfo: HotelDisplayInfo,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        HotelDateInfo(
            title = stringResource(id = CommonStringRes.check_in),
            date = hotelDisplayInfo.checkInDate
        )

        HotelDateDivider(
            description = pluralStringResource(id = CommonStringPluralsRes.numberOfNights, count = hotelDisplayInfo.duration, hotelDisplayInfo.duration)
        )

        HotelDateInfo(
            title = stringResource(id = CommonStringRes.check_out),
            date = hotelDisplayInfo.checkOutDate
        )
    }
}

@Composable
fun HotelDateDivider(
    description: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = description,
            style = typography.labelSmall,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1
        )

        Icon(
            modifier = Modifier.size(24.dp),
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
    hotelDisplayInfo: HotelDisplayInfo,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        HotelAdditionalInfoRow(
            title = stringResource(id = CommonStringRes.address),
            description = hotelDisplayInfo.address
        )

        Spacer(modifier = Modifier.height(4.dp))

        HotelAdditionalInfoRow(
            title = stringResource(id = CommonStringRes.prices),
            description = hotelDisplayInfo.price
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