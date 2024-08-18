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
import com.minhhnn18898.core.utils.DateTimeUtils
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfoModel
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightWithAirportInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.HotelInfo
import com.minhhnn18898.letstravel.tripdetail.domain.flight.GetListFlightInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.hotel.GetListHotelInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.trip.GetTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.presentation.CoverDefaultResourceProvider
import com.minhhnn18898.letstravel.tripinfo.presentation.TripItemDisplay
import com.minhhnn18898.letstravel.tripinfo.presentation.toTripItemDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val defaultResourceProvider: CoverDefaultResourceProvider,
    private val getTripInfoUseCase: GetTripInfoUseCase,
    private val getListFlightInfoUseCase: GetListFlightInfoUseCase,
    private val dateTimeUtils: DateTimeUtils = DateTimeUtils(),
    private val getListHotelInfoUseCase: GetListHotelInfoUseCase
): ViewModel() {

    val tripId = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1

    var tripInfoContentState: UiState<TripItemDisplay, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var flightInfoContentState: UiState<List<FlightDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var hotelInfoContentState: UiState<List<HotelDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    private var estimateBudget: MutableMap<BudgetType, Long> = mutableMapOf()
    var estimateBudgetDisplay by mutableStateOf("")
        private set

    init {
        loadTripInfo(tripId)
        loadFlightInfo(tripId)
        loadHotelInfo(tripId)
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

    private suspend fun handleResultLoadTripInfo(flowData: Flow<TripInfo>) {
        flowData.collect { item ->
            tripInfoContentState = UiState.Success(item.toTripItemDisplay(defaultResourceProvider))
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

    private fun AirportInfoModel.toAirportDisplayInfo(): AirportDisplayInfo {
        return AirportDisplayInfo(this.city, this.code, this.airportName)
    }

    private fun FlightWithAirportInfo.toFlightDisplayInfo(): FlightDisplayInfo {
        return FlightDisplayInfo(
            flightId = flightInfo.flightId,
            flightNumber = flightInfo.flightNumber,
            departAirport = departAirport.toAirportDisplayInfo(),
            destinationAirport = destinationAirport.toAirportDisplayInfo(),
            operatedAirlines = flightInfo.operatedAirlines,
            departureTime = dateTimeUtils.getFormatFlightDateTimeString(flightInfo.departureTime),
            arrivalTime = dateTimeUtils.getFormatFlightDateTimeString(flightInfo.arrivalTime),
            duration = calculateFlightDuration(flightInfo.departureTime, flightInfo.arrivalTime),
            price = flightInfo.price.formatWithCommas()
        )
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

    private fun HotelInfo.toHotelDisplayInfo(): HotelDisplayInfo {
        return HotelDisplayInfo(
            hotelId = this.hotelId,
            hotelName = this.hotelName,
            address = this.address,
            checkInDate = dateTimeUtils.millisToDateString(this.checkInDate),
            checkOutDate = dateTimeUtils.millisToDateString(this.checkOutDate),
            duration = calculateHotelStayDuration(this.checkInDate, this.checkOutDate),
            price = this.price.formatWithCommas()
        )
    }

    private fun Long.formatWithCommas(): String {
        return "%,d".format(this)
    }

    private fun calculateFlightDuration(from: Long, to: Long): String {
        return dateTimeUtils.getDurationInHourMinuteDisplayString(from, to)
    }

    private fun calculateHotelStayDuration(from: Long, to: Long): Int {
        return dateTimeUtils.getNightDuration(from, to).toInt()
    }

    private fun onUpdateFlightBudget(flightInfo: List<FlightWithAirportInfo>) {
        estimateBudget[BudgetType.FLIGHT] = flightInfo.calculateFlightTotalPrices()
        onUpdateBudgetTotal()
    }

    private fun onUpdateHotelBudget(hotelInfo: List<HotelInfo>) {
        estimateBudget[BudgetType.HOTEL] = hotelInfo.calculateHotelTotalPrices()
        onUpdateBudgetTotal()
    }

    private fun onUpdateBudgetTotal() {
        val total = estimateBudget.getTotal()
        estimateBudgetDisplay = if(total > 0) total.formatWithCommas() else ""
    }

    enum class BudgetType {
        FLIGHT,
        HOTEL,
    }
}

private fun List<HotelInfo>.calculateHotelTotalPrices(): Long {
    return this.sumOf { it.price }
}

private fun List<FlightWithAirportInfo>.calculateFlightTotalPrices(): Long {
    return this.sumOf { it.flightInfo.price }
}

private fun MutableMap<TripDetailScreenViewModel.BudgetType, Long>.getTotal(): Long {
    return this.values.sumOf { it }
}