package com.minhhnn18898.letstravel.tripdetail.presentation.trip

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
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightWithAirportInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.HotelInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.TripActivityInfo
import com.minhhnn18898.letstravel.tripdetail.domain.activity.GetListTripActivityInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.flight.GetListFlightInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.hotel.GetListHotelInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.presentation.activity.TripActivityDateTimeFormatter
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.domain.GetTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.presentation.base.CoverDefaultResourceProvider
import com.minhhnn18898.letstravel.tripinfo.presentation.base.UserTripDisplay
import com.minhhnn18898.letstravel.tripinfo.presentation.base.toTripItemDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val defaultResourceProvider: CoverDefaultResourceProvider,
    private val getTripInfoUseCase: GetTripInfoUseCase,
    private val getListFlightInfoUseCase: GetListFlightInfoUseCase,
    private val baseDateTimeFormatter: BaseDateTimeFormatter,
    private val activityDateTimeFormatter: TripActivityDateTimeFormatter,
    private val getListHotelInfoUseCase: GetListHotelInfoUseCase,
    private val getListTripActivityInfoUseCase: GetListTripActivityInfoUseCase
): ViewModel() {

    val tripId = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1

    var tripInfoContentState: UiState<UserTripDisplay, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var flightInfoContentState: UiState<List<FlightDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var hotelInfoContentState: UiState<List<HotelDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var activityInfoContentState: UiState<List<TripActivityDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    private var estimateBudget: MutableMap<BudgetType, Long> = mutableMapOf()
    var estimateBudgetDisplay by mutableStateOf("")
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
                tripInfoContentState = UiState.Success(item.toTripItemDisplay(defaultResourceProvider))
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
            getListTripActivityInfoUseCase.execute(GetListTripActivityInfoUseCase.Param(tripId))?.collect {
                when(it) {
                    is Result.Loading -> activityInfoContentState = UiState.Loading
                    is Result.Success -> handleResultLoadActivityInfo(it.data)
                    is Result.Error -> activityInfoContentState = UiState.Error(UiState.UndefinedError)
                }
            }
        }
    }

    private suspend fun handleResultLoadActivityInfo(flowData: Flow<List<TripActivityInfo>>) {
        flowData.collect { item ->
            activityInfoContentState = UiState.Success(item.map { it.toActivityDisplayInfo() })
            onUpdateActivityBudget(item)
        }
    }

    private fun Long.formatWithCommas(): String {
        return "%,d".format(this)
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
    }

    enum class BudgetType {
        FLIGHT,
        HOTEL,
        ACTIVITY
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
        return TripActivityDisplayInfo(
            activityId = this.activityId,
            title = this.title,
            photo = this.photo,
            description = this.description,
            date = activityDateTimeFormatter.millisToDateString(this.timeFrom),
            startTime = activityDateTimeFormatter.getHourMinuteFormatted(this.timeFrom),
            endTime = activityDateTimeFormatter.getHourMinuteFormatted(this.timeTo),
            price = this.price.formatWithCommas(),
        )
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

private fun MutableMap<TripDetailScreenViewModel.BudgetType, Long>.getTotal(): Long {
    return this.values.sumOf { it }
}