package com.minhhnn18898.letstravel.tripdetail.ui.trip

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
import com.minhhnn18898.letstravel.tripdetail.usecase.GetFlightInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.usecase.GetHotelInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.usecase.GetTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.letstravel.tripinfo.ui.CoverDefaultResourceProvider
import com.minhhnn18898.letstravel.tripinfo.ui.UserTripItemDisplay
import com.minhhnn18898.letstravel.tripinfo.ui.toTripItemDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripDetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val defaultResourceProvider: CoverDefaultResourceProvider,
    private val getTripInfoUseCase: GetTripInfoUseCase,
    private val getFlightInfoUseCase: GetFlightInfoUseCase,
    private val dateTimeUtils: DateTimeUtils = DateTimeUtils(),
    private val getHotelInfoUseCase: GetHotelInfoUseCase
): ViewModel() {

    val tripId = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1

    var tripInfoContentState: UiState<UserTripItemDisplay, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var flightInfoContentState: UiState<List<FlightDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    var hotelInfoContentState: UiState<List<HotelDisplayInfo>, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
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

    private suspend fun handleResultLoadTripInfo(flowData: Flow<TripInfoModel>) {
        flowData.collect { item ->
            tripInfoContentState = UiState.Success(item.toTripItemDisplay(defaultResourceProvider))
        }
    }

    private fun loadFlightInfo(tripId: Long) {
        viewModelScope.launch {
            getFlightInfoUseCase.execute(GetFlightInfoUseCase.Param(tripId))?.collect {
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
        }
    }

    private fun AirportInfoModel.toAirportDisplayInfo(): AirportDisplayInfo {
        return AirportDisplayInfo(this.city, this.code, this.airportName)
    }

    private fun FlightWithAirportInfo.toFlightDisplayInfo(): FlightDisplayInfo {
        return FlightDisplayInfo(
            flightInfo.flightNumber,
            departAirport.toAirportDisplayInfo(),
            destinationAirport.toAirportDisplayInfo(),
            flightInfo.operatedAirlines,
            dateTimeUtils.getFormatDateTimeString(flightInfo.departureTime),
            dateTimeUtils.getFormatDateTimeString(flightInfo.arrivalTime),
            calculateDuration(flightInfo.departureTime, flightInfo.arrivalTime),
            flightInfo.price.formatWithCommas()
        )
    }

    private fun loadHotelInfo(tripId: Long) {
        viewModelScope.launch {
            getHotelInfoUseCase.execute(GetHotelInfoUseCase.Param(tripId))?.collect {
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
        }
    }

    private fun HotelInfo.toHotelDisplayInfo(): HotelDisplayInfo {
        return HotelDisplayInfo("","","","","","")
    }

    private fun Long.formatWithCommas(): String {
        return "%,d".format(this)
    }

    private fun calculateDuration(from: Long, to: Long): String {
        return dateTimeUtils.getDurationInHourMinuteDisplayString(from, to)
    }
}