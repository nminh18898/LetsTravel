package com.minhhnn18898.manage_trip.tripdetail.presentation.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.core.utils.formatWithCommas
import com.minhhnn18898.manage_trip.tripdetail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.tripdetail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.tripdetail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.tripdetail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.tripdetail.domain.activity.GetSortedListTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.tripdetail.domain.flight.GetListFlightInfoUseCase
import com.minhhnn18898.manage_trip.tripdetail.domain.hotel.GetListHotelInfoUseCase
import com.minhhnn18898.manage_trip.tripdetail.presentation.activity.TripActivityDateTimeFormatter
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfo
import com.minhhnn18898.manage_trip.tripinfo.domain.GetTripInfoUseCase
import com.minhhnn18898.manage_trip.tripinfo.presentation.base.CoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.tripinfo.presentation.base.TripActivityDateSeparatorResourceProvider
import com.minhhnn18898.manage_trip.tripinfo.presentation.base.UserTripDisplay
import com.minhhnn18898.manage_trip.tripinfo.presentation.base.toTripItemDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val coverResourceProvider: CoverDefaultResourceProvider,
    private val activityDateSeparatorResourceProvider: TripActivityDateSeparatorResourceProvider,
    private val getTripInfoUseCase: GetTripInfoUseCase,
    private val getListFlightInfoUseCase: GetListFlightInfoUseCase,
    private val baseDateTimeFormatter: BaseDateTimeFormatter,
    private val activityDateTimeFormatter: TripActivityDateTimeFormatter,
    private val getListHotelInfoUseCase: GetListHotelInfoUseCase,
    private val getSortedListTripActivityInfoUseCase: GetSortedListTripActivityInfoUseCase
): ViewModel() {

    val tripId = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1

    var tripInfoContentState: UiState<UserTripDisplay, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var flightInfoContentState: UiState<List<FlightDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var hotelInfoContentState: UiState<List<HotelDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var activityInfoContentState: UiState<List<ITripActivityDisplay>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    private var estimateBudget: MutableMap<BudgetType, Long> = mutableMapOf()
    var estimateBudgetDisplay by mutableStateOf("")
        private set

    var budgetDisplay by mutableStateOf(BudgetDisplay(total = 0, portions = emptyList()))
        private set

    private val _eventChannel = Channel<Event>()
    val eventTriggerer = _eventChannel.receiveAsFlow()

    init {
        loadTripInfo(tripId)
        loadFlightInfo(tripId)
        loadHotelInfo(tripId)
        loadActivityInfo(tripId)
    }

    private fun loadTripInfo(tripId: Long) {
        viewModelScope.launch {
            getTripInfoUseCase.execute(GetTripInfoUseCase.Param(tripId))?.collect {
                when(it) {
                    is Result.Loading -> tripInfoContentState = UiState.Loading
                    is Result.Success -> handleResultLoadTripInfo(it.data)
                    is Result.Error -> tripInfoContentState = UiState.Error(UiState.UndefinedError)
                }
            }
        }
    }

    private suspend fun handleResultLoadTripInfo(flowData: Flow<TripInfo?>) {
        flowData.collect { item ->
            if(item != null) {
                tripInfoContentState = UiState.Success(item.toTripItemDisplay(coverResourceProvider))
            }
            else {
                _eventChannel.send(Event.CloseScreen)
           }
        }
    }

    private fun loadFlightInfo(tripId: Long) {
        viewModelScope.launch {
            getListFlightInfoUseCase.execute(GetListFlightInfoUseCase.Param(tripId))?.collect {
                when(it) {
                    is Result.Loading -> flightInfoContentState = UiState.Loading
                    is Result.Success -> handleResultLoadFlightInfo(it.data)
                    is Result.Error -> flightInfoContentState = UiState.Error(UiState.UndefinedError)
                }
            }
        }
    }

    private suspend fun handleResultLoadFlightInfo(flowData: Flow<List<FlightWithAirportInfo>>) {
        flowData.collect { item ->
            flightInfoContentState = UiState.Success(item.map { it.toFlightDisplayInfo() })
            onUpdateFlightBudget(item)
        }
    }

    private fun AirportInfo.toAirportDisplayInfo(): AirportDisplayInfo {
        return AirportDisplayInfo(this.city, this.code, this.airportName)
    }

    private fun loadHotelInfo(tripId: Long) {
        viewModelScope.launch {
            getListHotelInfoUseCase.execute(GetListHotelInfoUseCase.Param(tripId))?.collect {
                when(it) {
                    is Result.Loading -> hotelInfoContentState = UiState.Loading
                    is Result.Success -> handleResultLoadHotelInfo(it.data)
                    is Result.Error -> hotelInfoContentState = UiState.Error(UiState.UndefinedError)
                }
            }
        }
    }

    private suspend fun handleResultLoadHotelInfo(flowData: Flow<List<HotelInfo>>) {
        flowData.collect { item ->
            hotelInfoContentState = UiState.Success(item.map { it.toHotelDisplayInfo() })
            onUpdateHotelBudget(item)
        }
    }

    private fun loadActivityInfo(tripId: Long) {
        viewModelScope.launch {
            getSortedListTripActivityInfoUseCase.execute(GetSortedListTripActivityInfoUseCase.Param(tripId))?.collect {
                when(it) {
                    is Result.Loading -> activityInfoContentState = UiState.Loading
                    is Result.Success -> handleResultLoadActivityInfo(it.data)
                    is Result.Error -> activityInfoContentState = UiState.Error(UiState.UndefinedError)
                }
            }
        }
    }

    private suspend fun handleResultLoadActivityInfo(flowData: Flow<Map<Long?, List<TripActivityInfo>>>) {
        flowData.collect { item ->
            val itemRender = mutableListOf<ITripActivityDisplay>()
            var countDay = 1
            item.forEach { (date, activities) ->
                if (date != null) {
                    itemRender.add(
                        TripActivityDateGroupHeader(
                            title = activityDateTimeFormatter.getFormattedDateSeparatorString(date),
                            dateOrdering = countDay,
                            resId = activityDateSeparatorResourceProvider.getResource(countDay)
                        )
                    )
                    countDay++
                }

                itemRender.addAll(activities.toActivityDisplayInfo())
            }

            activityInfoContentState = UiState.Success(itemRender)
            onUpdateActivityBudget(item.values.flatten())
        }
    }

    private fun calculateFlightDuration(from: Long, to: Long): String {
        return baseDateTimeFormatter.getDurationInHourMinuteDisplayString(from, to)
    }

    private fun calculateHotelStayDuration(from: Long, to: Long): Int {
        return baseDateTimeFormatter.getNightDuration(from, to).toInt()
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
        estimateBudgetDisplay = if(total > 0) total.formatWithCommas() else ""
        budgetDisplay = BudgetDisplay(
            total = total,
            portions = estimateBudget.map { BudgetPortion(it.key, it.value) }
        )
    }

    sealed class Event {
        data object CloseScreen: Event()
    }

    private fun FlightWithAirportInfo.toFlightDisplayInfo(): FlightDisplayInfo {
        return FlightDisplayInfo(
            flightId = flightInfo.flightId,
            flightNumber = flightInfo.flightNumber,
            departAirport = departAirport.toAirportDisplayInfo(),
            destinationAirport = destinationAirport.toAirportDisplayInfo(),
            operatedAirlines = flightInfo.operatedAirlines,
            departureTime = baseDateTimeFormatter.getFormatFlightDateTimeString(flightInfo.departureTime),
            arrivalTime = baseDateTimeFormatter.getFormatFlightDateTimeString(flightInfo.arrivalTime),
            duration = calculateFlightDuration(flightInfo.departureTime, flightInfo.arrivalTime),
            price = flightInfo.price.formatWithCommas()
        )
    }

    private fun HotelInfo.toHotelDisplayInfo(): HotelDisplayInfo {
        return HotelDisplayInfo(
            hotelId = this.hotelId,
            hotelName = this.hotelName,
            address = this.address,
            checkInDate = baseDateTimeFormatter.millisToDateString(this.checkInDate),
            checkOutDate = baseDateTimeFormatter.millisToDateString(this.checkOutDate),
            duration = calculateHotelStayDuration(this.checkInDate, this.checkOutDate),
            price = this.price.formatWithCommas()
        )
    }

    private fun TripActivityInfo.toActivityDisplayInfo(): TripActivityDisplayInfo {
        val dateString = if(this.timeFrom != null) activityDateTimeFormatter.millisToDateString(this.timeFrom) else ""
        val startTimeString =  if(this.timeFrom != null) activityDateTimeFormatter.getHourMinuteFormatted(this.timeFrom) else ""
        val endTimeString = if(this.timeTo != null) activityDateTimeFormatter.getHourMinuteFormatted(this.timeTo) else ""

        return TripActivityDisplayInfo(
            activityId = this.activityId,
            title = this.title,
            photo = this.photo,
            description = this.description,
            date = dateString,
            startTime = startTimeString,
            endTime = endTimeString,
            price = if(this.price > 0) this.price.formatWithCommas() else "",
        )
    }

    private fun List<TripActivityInfo>.toActivityDisplayInfo(): List<TripActivityDisplayInfo> {
        return this.map { it.toActivityDisplayInfo() }
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