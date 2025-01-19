package com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.core.utils.formatWithCommas
import com.minhhnn18898.manage_trip.R
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.activity.renderTripActivitySection
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.flight.FlightDetailBody
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.hotel.HotelDetailBody
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.DetailSection
import com.minhhnn18898.ui_components.chart.PieChartData
import com.minhhnn18898.ui_components.chart.PieChartItem
import com.minhhnn18898.ui_components.chart.PieChartWithLabel
import com.minhhnn18898.ui_components.base_components.SectionCtaData
import com.minhhnn18898.ui_components.theme.typography

fun LazyListScope.renderPlanTabUI(
    flightContentState: UiState<List<FlightDisplayInfo>>,
    hotelContentState: UiState<List<HotelDisplayInfo>>,
    activityContentState: UiState<List<ITripActivityDisplay>>,
    budgetDisplay: BudgetDisplay,
    tripId: Long,
    onNavigateToEditFlightInfoScreen: (tripId: Long, flightId: Long) -> Unit,
    onNavigateEditHotelScreen: (tripId: Long, flightId: Long) -> Unit,
    onNavigateEditTripActivityScreen: (tripId: Long, activityId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    item {
        EstimatedBudgetSection(
            modifier = modifier,
            budgetDisplay = budgetDisplay
        )
    }

    item {
        DetailSection(
            icon = R.drawable.flight_takeoff_24,
            title = com.minhhnn18898.core.R.string.flights,
            modifier = modifier
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            FlightDetailBody(
                flightContentState,
                modifier = modifier,
                onClickCreateNewFlight = {
                    onNavigateToEditFlightInfoScreen.invoke(tripId, 0L)
                },
                onClickFlightInfoItem = { flightId ->
                    onNavigateToEditFlightInfoScreen.invoke(tripId, flightId)
                }
            )
        }
    }

    item {
        DetailSection(
            icon = R.drawable.hotel_24,
            title = com.minhhnn18898.core.R.string.hotels,
            modifier = modifier
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            HotelDetailBody(
                hotelInfoContentState = hotelContentState,
                modifier = modifier,
                onClickCreateHotelInfo = {
                    onNavigateEditHotelScreen.invoke(tripId, 0L)
                },
                onClickHotelInfoItem = { hotelId ->
                    onNavigateEditHotelScreen.invoke(tripId, hotelId)
                }
            )
        }
    }

    item {
        DetailSection(
            icon = R.drawable.nature_people_24,
            title = com.minhhnn18898.core.R.string.activities,
            sectionCtaData = SectionCtaData(
                icon = com.minhhnn18898.ui_components.R.drawable.add_24,
                title = com.minhhnn18898.core.R.string.add,
                onClick = {
                    onNavigateEditTripActivityScreen.invoke(tripId, 0L)
                }
            ),
            modifier = modifier)
    }

    renderTripActivitySection(
        activityInfoContentState = activityContentState,
        onClickCreateTripActivity = {
            onNavigateEditTripActivityScreen.invoke(tripId, 0L)
        },
        onClickActivityItem = { activityId ->
            onNavigateEditTripActivityScreen.invoke(tripId, activityId)
        },
        modifier = modifier
    )
}

@Composable
private fun EstimatedBudgetSection(
    modifier: Modifier = Modifier,
    budgetDisplay: BudgetDisplay
) {

    if(budgetDisplay.total > 0) {
        Column {
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
                    text = stringResource(com.minhhnn18898.core.R.string.estimated_budget),
                    style = typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            val pieChartItems = getPieChartItems(context = LocalContext.current, budgetDisplay)
            PieChartWithLabel(
                chartSize = 132.dp,
                data = PieChartData(
                    items = pieChartItems,
                    title = budgetDisplay.total.formatWithCommas()
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}


private fun getBudgetLabel(context: Context, type: BudgetType): String {
    return when(type) {
        BudgetType.FLIGHT -> StringUtils.getString(context, id = com.minhhnn18898.core.R.string.flight)
        BudgetType.HOTEL -> StringUtils.getString(context, id = com.minhhnn18898.core.R.string.hotel)
        BudgetType.ACTIVITY -> StringUtils.getString(context, id = com.minhhnn18898.core.R.string.activity)
    }
}

private val pieChartColor = mutableListOf(
    Color(0xFF4CB140),
    Color(0xFF5752D1),
    Color(0xFFF4C145),
    Color(0xFFEF9234),
    Color(0xFF519DE9),
    Color(0xFFC9190B)
)

fun getPieChartColor(index: Int): Color {
    if(index in 0 until pieChartColor.size) {
        return pieChartColor[index]
    }

    return Color(0xFFFFA600)
}

fun getPieChartItems(context: Context, budgetDisplay: BudgetDisplay): List<PieChartItem> {
    val pieChartItems = mutableListOf<PieChartItem>()

    for(i in budgetDisplay.portions.indices) {
        val portion = budgetDisplay.portions[i]
        val percent = (portion.price.toFloat() / budgetDisplay.total * 100)
        pieChartItems.add(
            PieChartItem(
                color = getPieChartColor(i),
                value = portion.price.toFloat(),
                percent = percent,
                label = getBudgetLabel(context, portion.type)
            )
        )
    }

    return pieChartItems
}