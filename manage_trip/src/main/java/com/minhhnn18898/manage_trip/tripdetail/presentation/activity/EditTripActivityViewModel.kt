package com.minhhnn18898.manage_trip.tripdetail.presentation.activity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.manage_trip.tripdetail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.tripdetail.domain.activity.CreateTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.tripdetail.domain.activity.DeleteTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.tripdetail.domain.activity.GetTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.tripdetail.domain.activity.UpdateTripActivityInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTripActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createTripActivityInfoUseCase: CreateTripActivityInfoUseCase,
    private val getTripActivityInfoUseCase: GetTripActivityInfoUseCase,
    private val updateTripActivityInfoUseCase: UpdateTripActivityInfoUseCase,
    private val deleteTripActivityInfoUseCase: DeleteTripActivityInfoUseCase,
    private val dateTimeFormatter: TripActivityDateTimeFormatter
): ViewModel() {

    private var tripId: Long = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1
    private var activityId: Long = savedStateHandle.get<Long>(MainAppRoute.activityIdArg) ?: 0L

    var canDeleteInfo by mutableStateOf(activityId > 0L)

    var allowSaveContent by mutableStateOf(false)
        private set

    var onShowLoadingState by mutableStateOf(false)
        private set

    var onShowDialogDeleteConfirmation by mutableStateOf(false)
        private set

    var errorType by mutableStateOf(ErrorType.ERROR_MESSAGE_NONE)
        private set

    private val _eventChannel = Channel<Event>()
    val eventTriggerer = _eventChannel.receiveAsFlow()

    var uiState = mutableStateOf(EditTripActivityUiState())
        private set

    init {
        loadActivityInfo(activityId)
    }

    private fun loadActivityInfo(activityId: Long) {
        if(activityId <= 0 ) return

        viewModelScope.launch {
            getTripActivityInfoUseCase.execute(GetTripActivityInfoUseCase.Param(activityId))?.collect {
                onShowLoadingState = it == Result.Loading

                when(it) {
                    is Result.Success -> {
                        uiState.value = it.data.toTripActivityUiState()
                        checkAllowSaveContent()
                    }
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_LOAD_ACTIVITY_INFO)
                    else -> {
                        // do nothing
                    }
                }
            }
        }
    }

    fun onPhotoUpdated(value: String) {
        uiState.value = uiState.value.copy(photo = value)
    }

    fun onNameUpdated(value: String) {
        uiState.value = uiState.value.copy(name = value)
        checkAllowSaveContent()
    }

    fun onDescriptionUpdated(value: String) {
        uiState.value = uiState.value.copy(description = value)
    }

    fun onPricesUpdated(value: String) {
        uiState.value = uiState.value.copy(prices = value)
    }

    fun onDateUpdated(date: Long?) {
        uiState.value = uiState.value.copy(date = date)
    }

    fun onTimeFromUpdated(value: Pair<Int, Int>) {
        uiState.value = uiState.value.copy(timeFrom = value)
    }

    fun onTimeToUpdated(value: Pair<Int, Int>) {
        uiState.value = uiState.value.copy(timeTo = value)
        checkAllowSaveContent()
    }


    fun onDeleteClick() {
        onShowDialogDeleteConfirmation = true
    }

    fun onDeleteConfirm() {
        onShowDialogDeleteConfirmation = false

        viewModelScope.launch {
            deleteTripActivityInfoUseCase.execute(DeleteTripActivityInfoUseCase.Param(activityId))?.collect {
                onShowLoadingState = it == Result.Loading

                when(it) {
                    is Result.Success -> _eventChannel.send(Event.CloseScreen)
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_ACTIVITY_INFO)
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

    fun onSaveClick() {
        viewModelScope.launch {
            val activityInfo = uiState.value.toActivityInfo()
            if(isUpdateExistingInfo()) {
                updateActivityInfo(activityInfo)
            }
            else {
                createActivityInfo(activityInfo)
            }
        }
    }

    private suspend fun createActivityInfo(activityInfo: TripActivityInfo) {
        createTripActivityInfoUseCase.execute(
            CreateTripActivityInfoUseCase.Param(tripId, activityInfo)
        )?.collect {
            onShowLoadingState = it == Result.Loading

            when(it) {
                is Result.Success -> _eventChannel.send(Event.CloseScreen)
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_ACTIVITY_INFO)
                else -> {
                    // do nothing
                }
            }
        }
    }

    private suspend fun updateActivityInfo(activityInfo: TripActivityInfo) {
        updateTripActivityInfoUseCase.execute(
            UpdateTripActivityInfoUseCase.Param(tripId, activityInfo)
        )?.collect {
            onShowLoadingState = it == Result.Loading

            when(it) {
                is Result.Success -> _eventChannel.send(Event.CloseScreen)
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_ACTIVITY_INFO)
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun isUpdateExistingInfo(): Boolean {
        return activityId > 0L
    }

    private fun checkAllowSaveContent() {
        val isValidName =  uiState.value.name.isNotBlankOrEmpty()
        allowSaveContent = isValidName && isValidSchedule()
    }

    private fun isValidSchedule(): Boolean {
        return uiState.value.timeTo.sumAsTotalMinutes() >= uiState.value.timeFrom.sumAsTotalMinutes()
    }

    private fun Pair<Int, Int>.sumAsTotalMinutes(): Int {
        return this.first * 60 + this.second
    }

    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            this@EditTripActivityViewModel.errorType = errorType
            delay(3000)
            this@EditTripActivityViewModel.errorType = ErrorType.ERROR_MESSAGE_NONE
        }
    }

    data class EditTripActivityUiState(
        val photo: String = "",
        val name: String = "",
        val description: String = "",
        val prices: String = "",
        val date: Long? = null,
        val timeFrom: Pair<Int, Int> = Pair(0, 0),
        val timeTo: Pair<Int, Int> = Pair(0, 0)
    )

    private fun EditTripActivityUiState.toActivityInfo(): TripActivityInfo {
        val timeFrom: Long? = if(this.date == null)  null else getDateTimeMillis(this.date, this.timeFrom)
        val timeTo: Long? = if(this.date == null)  null else getDateTimeMillis(this.date, this.timeTo)

        return TripActivityInfo(
            activityId = activityId,
            title = this.name,
            description = this.description,
            photo = this.photo,
            timeFrom = timeFrom,
            timeTo = timeTo,
            price = this.prices.toLongOrNull() ?: 0L
        )
    }

    private fun TripActivityInfo.toTripActivityUiState(): EditTripActivityUiState {
        val timeFrom = if(this.timeFrom != null) dateTimeFormatter.getHourMinute(this.timeFrom) else Pair(0, 0)
        val timeTo = if(this.timeTo != null) dateTimeFormatter.getHourMinute(this.timeTo) else Pair(0, 0)

        return EditTripActivityUiState(
            photo = this.photo,
            name = this.title,
            description = this.description,
            prices = this.price.toString(),
            date = this.timeFrom,
            timeFrom = timeFrom,
            timeTo = timeTo
        )
    }

    private fun getDateTimeMillis(date: Long, time: Pair<Int, Int>): Long {
        return dateTimeFormatter.convertToLocalDateTimeMillis(date, time.first, time.second)
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_ADD_ACTIVITY_INFO,
        ERROR_MESSAGE_ACTIVITY_SCHEDULE_IS_NOT_VALID,
        ERROR_MESSAGE_CAN_NOT_LOAD_ACTIVITY_INFO,
        ERROR_MESSAGE_CAN_NOT_UPDATE_ACTIVITY_INFO,
        ERROR_MESSAGE_CAN_NOT_DELETE_ACTIVITY_INFO
    }

    sealed class Event {
        data object CloseScreen: Event()
    }
}