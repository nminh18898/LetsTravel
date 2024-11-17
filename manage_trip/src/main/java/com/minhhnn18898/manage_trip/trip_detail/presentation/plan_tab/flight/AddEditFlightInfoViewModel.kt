package com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.flight

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.CreateNewFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.DeleteFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.GetFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.UpdateFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.flight.AddEditFlightInfoViewModel.ItineraryType
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FlightInfoUiState(
    val flightNumber: String = "",
    val operatedAirlines: String = "",
    val prices: String = "",
    val airportCodes: Map<ItineraryType, String> = createEmptyItineraryStringMap(),
    val airportNames: Map<ItineraryType, String> = createEmptyItineraryStringMap(),
    val airportCities: Map<ItineraryType, String> = createEmptyItineraryStringMap(),
    val flightDate: Map<ItineraryType, Long?> = createEmptyItineraryNullableLongMap(),
    val flightTime: Map<ItineraryType, Pair<Int, Int>> = createEmptyItineraryPairIntMap()
)

data class AddEditFlightUiState(
    val flightUiState: FlightInfoUiState = FlightInfoUiState(),
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false,
    val canDelete: Boolean = false,
    val isShowDeleteConfirmation: Boolean = false,
    val showError: AddEditFlightInfoViewModel.ErrorType = AddEditFlightInfoViewModel.ErrorType.ERROR_MESSAGE_NONE,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val allowSaveContent: Boolean = false
)

private fun createEmptyItineraryStringMap(): Map<ItineraryType, String> = mutableMapOf(
    ItineraryType.DEPARTURE to "",
    ItineraryType.ARRIVAL to ""
)

private fun createEmptyItineraryNullableLongMap(): Map<ItineraryType, Long?> = mutableMapOf(
    ItineraryType.DEPARTURE to null,
    ItineraryType.ARRIVAL to null
)

private fun createEmptyItineraryPairIntMap(): Map<ItineraryType, Pair<Int, Int>> = mutableMapOf(
    ItineraryType.DEPARTURE to Pair(0, 0),
    ItineraryType.ARRIVAL to Pair(0, 0)
)

fun FlightWithAirportInfo.toFlightInfoUiState(dateTimeFormatter: TripDetailDateTimeFormatter): FlightInfoUiState {
    return FlightInfoUiState(
        flightNumber = this.flightInfo.flightNumber,
        operatedAirlines = this.flightInfo.operatedAirlines,
        prices = this.flightInfo.price.toString(),
        flightDate = mutableMapOf(
            ItineraryType.DEPARTURE to this.flightInfo.departureTime,
            ItineraryType.ARRIVAL to this.flightInfo.arrivalTime
        ),
        flightTime = mutableMapOf(
            ItineraryType.DEPARTURE to dateTimeFormatter.getHourMinute(this.flightInfo.departureTime),
            ItineraryType.ARRIVAL to dateTimeFormatter.getHourMinute(this.flightInfo.arrivalTime)
        ),
        airportCodes = mutableMapOf(
            ItineraryType.DEPARTURE to this.departAirport.code,
            ItineraryType.ARRIVAL to this.destinationAirport.code
        ),
        airportNames = mutableMapOf(
            ItineraryType.DEPARTURE to this.departAirport.airportName,
            ItineraryType.ARRIVAL to this.destinationAirport.airportName
        ),
        airportCities = mutableMapOf(
            ItineraryType.DEPARTURE to this.departAirport.city,
            ItineraryType.ARRIVAL to this.destinationAirport.city
        )
    )
}

@HiltViewModel
class AddEditFlightInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createNewFlightInfoUseCase: CreateNewFlightInfoUseCase,
    private val getFlightInfoUseCase: GetFlightInfoUseCase,
    private val updateFlightInfoUseCase: UpdateFlightInfoUseCase,
    private val deleteFlightInfoUseCase: DeleteFlightInfoUseCase,
    private val dateTimeFormatter: TripDetailDateTimeFormatter
): ViewModel() {

    private val tripId: Long = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1
    private val flightId: Long = savedStateHandle.get<Long>(MainAppRoute.flightIdArg) ?: 0L

    private val _uiState = MutableStateFlow(AddEditFlightUiState())
    val uiState: StateFlow<AddEditFlightUiState> = _uiState.asStateFlow()

    init {
        if(flightId > 0) {
            loadFlightInfo(flightId)
        }
    }

    private fun loadFlightInfo(flightId: Long) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getFlightInfoUseCase.execute(GetFlightInfoUseCase.Param(flightId)).collect { flightInfo ->
                if(flightInfo != null) {
                    _uiState.update {
                        it.copy(
                            flightUiState = flightInfo.toFlightInfoUiState(dateTimeFormatter),
                            isLoading = false,
                            isNotFound = false,
                            canDelete = true
                        )
                    }
                    checkAllowSaveContent()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNotFound = true,
                            canDelete = false
                        )
                    }
                }
            }
        }
    }

    fun onFlightNumberUpdated(value: String) {
        _uiState.update {
            it.copy(
                flightUiState = it.flightUiState.copy(flightNumber = value)
            )
        }
        checkAllowSaveContent()
    }

    fun onAirlinesUpdated(value: String) {
        _uiState.update {
            it.copy(
                flightUiState = it.flightUiState.copy(operatedAirlines = value)
            )
        }
    }

    fun onPricesUpdated(value: String) {
        _uiState.update {
            it.copy(
                flightUiState = it.flightUiState.copy(prices = value)
            )
        }
    }

    fun onAirportCodeUpdated(itineraryType: ItineraryType, value: String) {
        _uiState.update {
            val currentValue = it.flightUiState.airportCodes.toMutableMap()
            currentValue[itineraryType] = value
            it.copy(
                flightUiState = it.flightUiState.copy(airportCodes = currentValue)
            )
        }
        checkAllowSaveContent()
    }

    fun onAirportNameUpdated(itineraryType: ItineraryType, value: String) {
        _uiState.update {
            val currentValue = it.flightUiState.airportNames.toMutableMap()
            currentValue[itineraryType] = value
            it.copy(
                flightUiState = it.flightUiState.copy(airportNames = currentValue)
            )
        }
    }

    fun onAirportCityUpdated(itineraryType: ItineraryType, value: String) {
        _uiState.update {
            val currentValue = it.flightUiState.airportCities.toMutableMap()
            currentValue[itineraryType] = value
            it.copy(
                flightUiState = it.flightUiState.copy(airportCities = currentValue)
            )
        }
    }

    fun onFlightDateUpdated(itineraryType: ItineraryType, value: Long?) {
        _uiState.update {
            val currentValue = it.flightUiState.flightDate.toMutableMap()
            currentValue[itineraryType] = value
            it.copy(
                flightUiState = it.flightUiState.copy(flightDate = currentValue)
            )
        }

        if(itineraryType == ItineraryType.ARRIVAL) {
            checkInvalidFlightTimeRangeAndNotify()
        }

        checkAllowSaveContent()
    }

    fun onFlightTimeUpdated(itineraryType: ItineraryType, value: Pair<Int, Int>) {
        _uiState.update {
            val currentValue = it.flightUiState.flightTime.toMutableMap()
            currentValue[itineraryType] = value
            it.copy(
                flightUiState = it.flightUiState.copy(flightTime = currentValue)
            )
        }

        checkAllowSaveContent()
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
        _uiState.update {
            it.copy(allowSaveContent = isAllowSave())
        }
    }

    private fun isAllowSave(): Boolean {
        val flightUiState = uiState.value.flightUiState

        return flightUiState.flightNumber.isNotBlankOrEmpty() &&
                flightUiState.airportCodes[ItineraryType.DEPARTURE]?.isNotBlankOrEmpty() == true &&
                flightUiState.airportCodes[ItineraryType.ARRIVAL]?.isNotBlankOrEmpty() == true &&
                flightUiState.flightDate[ItineraryType.DEPARTURE] != null &&
                flightUiState.flightDate[ItineraryType.ARRIVAL] != null &&
                isValidFlightTime()
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val flightInfo = FlightInfo(
                flightId = flightId,
                flightNumber = uiState.value.flightUiState.flightNumber,
                operatedAirlines = uiState.value.flightUiState.operatedAirlines,
                departureTime = getDateTimeMillis(ItineraryType.DEPARTURE),
                arrivalTime = getDateTimeMillis(ItineraryType.ARRIVAL),
                price = uiState.value.flightUiState.prices.toLongOrNull() ?: 0L
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

    private suspend fun createNewFlightInfo(flightInfo: FlightInfo, departAirport: AirportInfo, arrivalAirport: AirportInfo) {
        createNewFlightInfoUseCase.execute(
            CreateNewFlightInfoUseCase.Param(
                tripId,
                flightInfo,
                departAirport,
                arrivalAirport
            )
        ).collect { result ->
            when(result) {
                is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isCreated = true
                        )
                    }
                }
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_FLIGHT_INFO)
            }
        }
    }

    private suspend fun updateFlightInfo(flightInfo: FlightInfo, departAirport: AirportInfo, arrivalAirport: AirportInfo) {
        updateFlightInfoUseCase.execute(
            UpdateFlightInfoUseCase.Param(
                tripId,
                flightInfo,
                departAirport,
                arrivalAirport
            )
        ).collect { result ->
            when(result) {
                is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isUpdated = true
                        )
                    }
                }
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_FLIGHT_INFO)
            }
        }
    }

    fun onDeleteClick() {
        _uiState.update {
            it.copy(isShowDeleteConfirmation = true)
        }
    }

    fun onDeleteConfirm() {
        _uiState.update {
            it.copy(isShowDeleteConfirmation = false)
        }

        viewModelScope.launch {
            deleteFlightInfoUseCase.execute(DeleteFlightInfoUseCase.Param(flightId)).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isDeleted = true
                            )
                        }
                    }
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_FLIGHT_INFO)
                }
            }
        }
    }

    fun onDeleteDismiss() {
        _uiState.update {
            it.copy(isShowDeleteConfirmation = false)
        }
    }

    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    showError = errorType
                )
            }
            delay(3000)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    showError = ErrorType.ERROR_MESSAGE_NONE
                )
            }
        }
    }

    private fun extractAirportInfoFromInput(type: ItineraryType): AirportInfo {
        return AirportInfo(
            code = uiState.value.flightUiState.airportCodes[type] ?: "",
            city = uiState.value.flightUiState.airportCities[type] ?: "",
            airportName = uiState.value.flightUiState.airportNames[type] ?: ""
        )
    }

    private fun getDateTimeMillis(type: ItineraryType): Long {
        val localDate = uiState.value.flightUiState.flightDate[type] ?: 0L
        val timeHourMinutes = uiState.value.flightUiState.flightTime[type] ?: Pair(0, 0)

        return dateTimeFormatter.combineHourMinutesDayToMillis(localDate, timeHourMinutes.first, timeHourMinutes.second)
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
}