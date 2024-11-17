package com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.core.utils.formatWithCommas
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.GetSortedListTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.GetListFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.hotel.GetListHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ITripActivityDateSeparatorResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TripDetailPlanTabController(
    viewModelScope: CoroutineScope,
    tripId: Long,
    private val activityDateSeparatorResourceProvider: ITripActivityDateSeparatorResourceProvider,
    private val dateTimeFormatter: TripDetailDateTimeFormatter,
    getListFlightInfoUseCase: GetListFlightInfoUseCase,
    getListHotelInfoUseCase: GetListHotelInfoUseCase,
    getSortedListTripActivityInfoUseCase: GetSortedListTripActivityInfoUseCase
) {

    val flightInfoContentState: StateFlow<UiState<List<FlightDisplayInfo>>> =
        getListFlightInfoUseCase.execute(GetListFlightInfoUseCase.Param(tripId))
            .map { flightInfo ->
                onUpdateFlightBudget(flightInfo)
                UiState.Success(flightInfo.map { it.toFlightDisplayInfo() })
            }
            .catch<UiState<List<FlightDisplayInfo>>> {
                emit(UiState.Error())
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = UiState.Loading
            )

    val hotelInfoContentState: StateFlow<UiState<List<HotelDisplayInfo>>> =
        getListHotelInfoUseCase.execute(GetListHotelInfoUseCase.Param(tripId))
            .map { hotelInfo ->
                onUpdateHotelBudget(hotelInfo)
                UiState.Success(hotelInfo.map { it.toHotelDisplayInfo() })
            }
            .catch<UiState<List<HotelDisplayInfo>>> {
                emit(UiState.Error())
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = UiState.Loading
            )

    val activityInfoContentState: StateFlow<UiState<List<ITripActivityDisplay>>> =
        getSortedListTripActivityInfoUseCase.execute(GetSortedListTripActivityInfoUseCase.Param(tripId))
            .map {
                onUpdateActivityBudget(it.values.flatten())
                UiState.Success(it.makeListTripActivityDisplay())
            }
            .catch<UiState<List<ITripActivityDisplay>>> {
                emit(UiState.Error())
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = UiState.Loading
            )

    private var estimateBudget: MutableMap<BudgetType, Long> = mutableMapOf()
    var budgetDisplay by mutableStateOf(BudgetDisplay(total = 0, portions = emptyList()))
        private set

    private fun AirportInfo.toAirportDisplayInfo(): AirportDisplayInfo {
        return AirportDisplayInfo(this.city, this.code, this.airportName)
    }

    private fun calculateFlightDuration(from: Long, to: Long): String {
        return dateTimeFormatter.findFlightDurationFormattedString(from, to)
    }

    private fun calculateHotelStayDuration(from: Long, to: Long): Int {
        return dateTimeFormatter.getHotelNights(from, to).toInt()
    }

    private fun onUpdateFlightBudget(flightInfo: List<FlightWithAirportInfo>) {
        estimateBudget[BudgetType.FLIGHT] = flightInfo.calculateFlightTotalPrices()
        onUpdateBudgetTotal()
    }

    private fun onUpdateHotelBudget(hotelInfo: List<HotelInfo>) {
        estimateBudget[BudgetType.HOTEL] = hotelInfo.calculateHotelTotalPrices()
        onUpdateBudgetTotal()
    }

    private fun onUpdateActivityBudget(activityInfo: List<TripActivityInfo>) {
        estimateBudget[BudgetType.ACTIVITY] = activityInfo.calculateActivityTotalPrices()
        onUpdateBudgetTotal()
    }

    private fun onUpdateBudgetTotal() {
        val total = estimateBudget.getTotal()
        budgetDisplay = BudgetDisplay(
            total = total,
            portions = estimateBudget.map { BudgetPortion(it.key, it.value) }
        )
    }

    private fun FlightWithAirportInfo.toFlightDisplayInfo(): FlightDisplayInfo {
        return FlightDisplayInfo(
            flightId = flightInfo.flightId,
            flightNumber = flightInfo.flightNumber,
            departAirport = departAirport.toAirportDisplayInfo(),
            destinationAirport = destinationAirport.toAirportDisplayInfo(),
            operatedAirlines = flightInfo.operatedAirlines,
            departureTime = dateTimeFormatter.getFormattedFlightDateTimeString(flightInfo.departureTime),
            arrivalTime = dateTimeFormatter.getFormattedFlightDateTimeString(flightInfo.arrivalTime),
            duration = calculateFlightDuration(flightInfo.departureTime, flightInfo.arrivalTime),
            price = flightInfo.price.formatWithCommas()
        )
    }

    private fun HotelInfo.toHotelDisplayInfo(): HotelDisplayInfo {
        return HotelDisplayInfo(
            hotelId = this.hotelId,
            hotelName = this.hotelName,
            address = this.address,
            checkInDate = dateTimeFormatter.millisToHotelFormattedString(this.checkInDate),
            checkOutDate = dateTimeFormatter.millisToHotelFormattedString(this.checkOutDate),
            duration = calculateHotelStayDuration(this.checkInDate, this.checkOutDate),
            price = this.price.formatWithCommas()
        )
    }

    private fun TripActivityInfo.toActivityDisplayInfo(): TripActivityDisplayInfo {
        val dateString = if(this.timeFrom != null) dateTimeFormatter.millisToActivityFormattedString(this.timeFrom) else ""
        val startTimeString =  if(this.timeFrom != null) dateTimeFormatter.formatHourMinutes(this.timeFrom) else ""
        val endTimeString = if(this.timeTo != null) dateTimeFormatter.formatHourMinutes(this.timeTo) else ""

        return TripActivityDisplayInfo(
            activityId = this.activityId,
            title = this.title,
            photo = this.photo,
            description = this.description,
            date = dateString,
            startTime = startTimeString,
            endTime = endTimeString,
            price = if(this.price > 0) this.price.formatWithCommas() else ""
        )
    }

    private fun List<TripActivityInfo>.toActivityDisplayInfo(): List<TripActivityDisplayInfo> {
        return this.map { it.toActivityDisplayInfo() }
    }

    private fun Map<Long?, List<TripActivityInfo>>.makeListTripActivityDisplay(): List<ITripActivityDisplay> {
        val itemRender = mutableListOf<ITripActivityDisplay>()
        var countDay = 1
        this.forEach { (date, activities) ->
            if (date != null) {
                itemRender.add(
                    TripActivityDateGroupHeader(
                        title = dateTimeFormatter.getActivityFormattedDateSeparatorString(date),
                        dateOrdering = countDay,
                        resId = activityDateSeparatorResourceProvider.getResource(countDay)
                    )
                )
                countDay++
            }

            itemRender.addAll(activities.toActivityDisplayInfo())
        }

        return itemRender
    }
}

private fun List<FlightWithAirportInfo>.calculateFlightTotalPrices(): Long {
    return this.sumOf { it.flightInfo.price }
}

private fun List<HotelInfo>.calculateHotelTotalPrices(): Long {
    return this.sumOf { it.price }
}

private fun List<TripActivityInfo>.calculateActivityTotalPrices(): Long {
    return this.sumOf { it.price }
}

private fun MutableMap<BudgetType, Long>.getTotal(): Long {
    return this.values.sumOf { it }
}