@file:OptIn(ExperimentalMaterial3Api::class)

package com.minhhnn18898.letstravel.tripdetail.presentation.flight

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfoModel
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightWithAirportInfo
import com.minhhnn18898.letstravel.tripdetail.domain.flight.CreateNewFlightInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.flight.DeleteFlightInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.flight.GetFlightInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.flight.UpdateFlightInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditFlightInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createNewFlightInfoUseCase: CreateNewFlightInfoUseCase,
    private val getFlightInfoUseCase: GetFlightInfoUseCase,
    private val updateFlightInfoUseCase: UpdateFlightInfoUseCase,
    private val deleteFlightInfoUseCase: DeleteFlightInfoUseCase,
    private val baseDateTimeFormatter: BaseDateTimeFormatter = BaseDateTimeFormatter()
): ViewModel() {

    private val tripId: Long = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1
    private val flightId: Long = savedStateHandle.get<Long>(MainAppRoute.flightIdArg) ?: 0L

    var flightNumber by mutableStateOf("")
        private set

    var operatedAirlines by mutableStateOf("")
        private set

    var prices by mutableStateOf("")
        private set

    private var airportCodes: Map<ItineraryType, MutableState<String>> = mutableMapOf(
        ItineraryType.DEPARTURE to mutableStateOf(""),
        ItineraryType.ARRIVAL to mutableStateOf("")
    )

    private var airportNames: Map<ItineraryType, MutableState<String>> = mutableMapOf(
        ItineraryType.DEPARTURE to mutableStateOf(""),
        ItineraryType.ARRIVAL to mutableStateOf("")
    )

    private var airportCities: Map<ItineraryType, MutableState<String>> = mutableMapOf(
        ItineraryType.DEPARTURE to mutableStateOf(""),
        ItineraryType.ARRIVAL to mutableStateOf("")
    )

    private var flightDate: Map<ItineraryType, MutableState<Long?>> = mutableMapOf(
        ItineraryType.DEPARTURE to mutableStateOf(null),
        ItineraryType.ARRIVAL to mutableStateOf(null)
    )

    private var flightTime: Map<ItineraryType, MutableState<Pair<Int, Int>>> = mutableMapOf(
        ItineraryType.DEPARTURE to mutableStateOf(Pair(0, 0)),
        ItineraryType.ARRIVAL to mutableStateOf(Pair(0, 0))
    )

    var allowSaveContent by mutableStateOf(false)
        private set

    var canDelete by mutableStateOf(flightId > 0L)

    var onShowLoadingState by mutableStateOf(false)
        private set

    var onShowDialogDeleteConfirmation by mutableStateOf(false)
        private set

    var errorType by mutableStateOf(ErrorType.ERROR_MESSAGE_NONE)
        private set

    private val _eventChannel = Channel<Event>()
    val eventTriggerer = _eventChannel.receiveAsFlow()

    init {
        loadFlightInfo(flightId)
    }

    private fun loadFlightInfo(flightId: Long) {
        if(flightId <= 0 ) return

        viewModelScope.launch {
            getFlightInfoUseCase.execute(GetFlightInfoUseCase.Param(flightId))?.collect {
                onShowLoadingState = it == Result.Loading

                when(it) {
                    is Result.Success -> handleResultLoadFlightInfo(it.data)
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_LOAD_FLIGHT_INFO)
                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    private fun handleResultLoadFlightInfo(flightInfoWithAirport: FlightWithAirportInfo) {
        val flightInfo = flightInfoWithAirport.flightInfo
        onFlightNumberUpdated(flightInfo.flightNumber)
        onAirlinesUpdated(flightInfo.operatedAirlines)
        onPricesUpdated(flightInfo.price.toString())
        onFlightDateUpdated(ItineraryType.DEPARTURE, flightInfo.departureTime)
        onFlightTimeUpdated(ItineraryType.DEPARTURE, baseDateTimeFormatter.getHourMinute(flightInfo.departureTime))
        onFlightDateUpdated(ItineraryType.ARRIVAL, flightInfo.arrivalTime, showWarningInvalidRange = false)
        onFlightTimeUpdated(ItineraryType.ARRIVAL, baseDateTimeFormatter.getHourMinute(flightInfo.arrivalTime), showWarningInvalidRange = false)

        val departAirport = flightInfoWithAirport.departAirport
        onAirportCodeUpdated(ItineraryType.DEPARTURE, departAirport.code)
        onAirportNameUpdated(ItineraryType.DEPARTURE, departAirport.airportName)
        onAirportCityUpdated(ItineraryType.DEPARTURE, departAirport.city)

        val destinationAirport = flightInfoWithAirport.destinationAirport
        onAirportCodeUpdated(ItineraryType.ARRIVAL, destinationAirport.code)
        onAirportNameUpdated(ItineraryType.ARRIVAL, destinationAirport.airportName)
        onAirportCityUpdated(ItineraryType.ARRIVAL, destinationAirport.city)
    }

    fun onFlightNumberUpdated(value: String) {
        flightNumber = value
        checkAllowSaveContent()
    }

    fun onAirlinesUpdated(value: String) {
        operatedAirlines = value
    }

    fun onPricesUpdated(value: String) {
        prices = value
    }

    fun getAirportCode(itineraryType: ItineraryType): String {
        return airportCodes[itineraryType]?.value ?: ""
    }

    fun onAirportCodeUpdated(itineraryType: ItineraryType, value: String) {
        airportCodes[itineraryType]?.value = value
        checkAllowSaveContent()
    }

    fun getAirportName(itineraryType: ItineraryType): String {
        return airportNames[itineraryType]?.value ?: ""
    }

    fun onAirportNameUpdated(itineraryType: ItineraryType, value: String) {
        airportNames[itineraryType]?.value = value
    }

    fun getAirportCity(itineraryType: ItineraryType): String {
        return airportCities[itineraryType]?.value ?: ""
    }

    fun onAirportCityUpdated(itineraryType: ItineraryType, value: String) {
        airportCities[itineraryType]?.value = value
    }

    fun getFlightDate(itineraryType: ItineraryType): Long? {
        return flightDate[itineraryType]?.value
    }

    fun onFlightDateUpdated(itineraryType: ItineraryType, value: Long?, showWarningInvalidRange: Boolean = true) {
        flightDate[itineraryType]?.value = value

        if(itineraryType == ItineraryType.ARRIVAL && showWarningInvalidRange) {
            checkInvalidFlightTimeRangeAndNotify()
        }
    }

    fun getFlightTime(itineraryType: ItineraryType): Pair<Int, Int> {
        return flightTime[itineraryType]?.value ?: Pair(0, 0)
    }

    fun onFlightTimeUpdated(itineraryType: ItineraryType, value: Pair<Int, Int>, showWarningInvalidRange: Boolean = true) {
        flightTime[itineraryType]?.value = value

        if(itineraryType == ItineraryType.ARRIVAL && showWarningInvalidRange) {
            checkInvalidFlightTimeRangeAndNotify()
        }
    }

    private fun checkInvalidFlightTimeRangeAndNotify() {
        if(!isValidFlightTime()) {
            showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_FLIGHT_TIME_IS_NOT_VALID)
        }
    }

    private fun isValidFlightTime(): Boolean {
        return getDateTimeMillis(ItineraryType.ARRIVAL) > getDateTimeMillis(ItineraryType.DEPARTURE)
    }

    private fun checkAllowSaveContent() {
        allowSaveContent =
            flightNumber.isNotBlankOrEmpty() &&
            airportCodes[ItineraryType.DEPARTURE]?.value?.isNotBlankOrEmpty() == true &&
            airportCodes[ItineraryType.ARRIVAL]?.value?.isNotBlankOrEmpty() == true &&
            flightDate[ItineraryType.DEPARTURE]?.value != null &&
            flightDate[ItineraryType.ARRIVAL]?.value != null &&
            isValidFlightTime()
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val flightInfo = FlightInfo(
                flightId = flightId,
                flightNumber,
                operatedAirlines,
                getDateTimeMillis(ItineraryType.DEPARTURE),
                getDateTimeMillis(ItineraryType.ARRIVAL),
                prices.toLongOrNull() ?: 0L
            )
            val departAirport = extractAirportInfoFromInput(ItineraryType.DEPARTURE)
            val arrivalAirport = extractAirportInfoFromInput(ItineraryType.ARRIVAL)

            if(isUpdateExistingInfo()) {
                updateFlightInfo(flightInfo, departAirport, arrivalAirport)
            } else {
                createNewFlightInfo(flightInfo, departAirport, arrivalAirport)
            }
        }
    }

    private fun isUpdateExistingInfo(): Boolean {
        return flightId > 0L
    }

    private suspend fun createNewFlightInfo(flightInfo: FlightInfo, departAirport: AirportInfoModel, arrivalAirport: AirportInfoModel) {
        createNewFlightInfoUseCase.execute(
            CreateNewFlightInfoUseCase.Param(
                tripId,
                flightInfo,
                departAirport,
                arrivalAirport
            )
        )?.collect {
            onShowLoadingState = it == Result.Loading

            when(it) {
                is Result.Success -> _eventChannel.send(Event.CloseScreen)
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_FLIGHT_INFO)
                else -> {
                    // do nothing
                }
            }
        }
    }

    private suspend fun updateFlightInfo(flightInfo: FlightInfo, departAirport: AirportInfoModel, arrivalAirport: AirportInfoModel) {
        updateFlightInfoUseCase.execute(
            UpdateFlightInfoUseCase.Param(
                tripId,
                flightInfo,
                departAirport,
                arrivalAirport
            )
        )?.collect {
            onShowLoadingState = it == Result.Loading

            when(it) {
                is Result.Success -> _eventChannel.send(Event.CloseScreen)
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_FLIGHT_INFO)
                else -> {
                    // do nothing
                }
            }
        }
    }

    fun onDeleteClick() {
        onShowDialogDeleteConfirmation = true
    }

    fun onDeleteConfirm() {
        onShowDialogDeleteConfirmation = false

        viewModelScope.launch {
            deleteFlightInfoUseCase.execute(DeleteFlightInfoUseCase.Param(flightId))?.collect {
                onShowLoadingState = it == Result.Loading

                when(it) {
                    is Result.Success -> _eventChannel.send(Event.CloseScreen)
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_FLIGHT_INFO)
                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    fun onDeleteDismiss() {
        onShowDialogDeleteConfirmation = false
    }

    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            this@EditFlightInfoViewModel.errorType = errorType
            delay(3000)
            this@EditFlightInfoViewModel.errorType = ErrorType.ERROR_MESSAGE_NONE
        }
    }

    private fun extractAirportInfoFromInput(type: ItineraryType): AirportInfoModel {
        return AirportInfoModel(
            airportCodes.getStringValue(type),
            airportCities.getStringValue(type),
            airportNames.getStringValue(type)
        )
    }

    private fun getDateTimeMillis(type: ItineraryType): Long {
        val localDate = flightDate[type]?.value ?: 0L
        val timeHourMinutes = flightTime[type]?.value ?: Pair(0, 0)

        return baseDateTimeFormatter.convertToLocalDateTimeMillis(localDate, timeHourMinutes.first, timeHourMinutes.second)
    }

    private fun Map<ItineraryType, MutableState<String>>.getStringValue(key: ItineraryType): String {
        return this[key]?.value ?: ""
    }

    enum class ItineraryType {
        DEPARTURE,
        ARRIVAL
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_ADD_FLIGHT_INFO,
        ERROR_MESSAGE_CAN_NOT_LOAD_FLIGHT_INFO,
        ERROR_MESSAGE_CAN_NOT_UPDATE_FLIGHT_INFO,
        ERROR_MESSAGE_CAN_NOT_DELETE_FLIGHT_INFO,
        ERROR_MESSAGE_FLIGHT_TIME_IS_NOT_VALID
    }

    sealed class Event {
        data object CloseScreen: Event()
    }
}