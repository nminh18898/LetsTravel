package com.minhhnn18898.manage_trip.trip_detail.presentation.activity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.CreateTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.DeleteTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.GetTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.UpdateTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TripActivityUiState(
    val photo: String = "",
    val name: String = "",
    val description: String = "",
    val prices: String = "",
    val date: Long? = null,
    val timeFrom: Pair<Int, Int> = Pair(0, 0),
    val timeTo: Pair<Int, Int> = Pair(0, 0)
)

data class AddEditTripActivityUiState(
    val tripActivityUiState: TripActivityUiState = TripActivityUiState(),
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false,
    val canDelete: Boolean = false,
    val isShowDeleteConfirmation: Boolean = false,
    val showError: AddEditTripActivityViewModel.ErrorType = AddEditTripActivityViewModel.ErrorType.ERROR_MESSAGE_NONE,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val allowSaveContent: Boolean = false
)

@HiltViewModel
class AddEditTripActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createTripActivityInfoUseCase: CreateTripActivityInfoUseCase,
    private val getTripActivityInfoUseCase: GetTripActivityInfoUseCase,
    private val updateTripActivityInfoUseCase: UpdateTripActivityInfoUseCase,
    private val deleteTripActivityInfoUseCase: DeleteTripActivityInfoUseCase,
    private val dateTimeFormatter: TripDetailDateTimeFormatter
): ViewModel() {

    private var tripId: Long = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1
    private var activityId: Long = savedStateHandle.get<Long>(MainAppRoute.activityIdArg) ?: 0L

    private val _uiState = MutableStateFlow(AddEditTripActivityUiState())
    val uiState: StateFlow<AddEditTripActivityUiState> = _uiState.asStateFlow()

    init {
        if(activityId > 0) {
            loadActivityInfo(activityId)
        }
    }

    private fun loadActivityInfo(activityId: Long) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getTripActivityInfoUseCase.execute(GetTripActivityInfoUseCase.Param(activityId)).collect { activityInfo ->
                if(activityInfo != null) {
                    _uiState.update {
                        it.copy(
                            tripActivityUiState = activityInfo.toTripActivityUiState(),
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

    fun onPhotoUpdated(value: String) {
        _uiState.update {
            it.copy(
                tripActivityUiState = it.tripActivityUiState.copy(photo = value)
            )
        }
    }

    fun onNameUpdated(value: String) {
        _uiState.update {
            it.copy(
                tripActivityUiState = it.tripActivityUiState.copy(name = value)
            )
        }
        checkAllowSaveContent()
    }

    fun onDescriptionUpdated(value: String) {
        _uiState.update {
            it.copy(
                tripActivityUiState = it.tripActivityUiState.copy(description = value)
            )
        }
    }

    fun onPricesUpdated(value: String) {
        _uiState.update {
            it.copy(
                tripActivityUiState = it.tripActivityUiState.copy(prices = value)
            )
        }
    }

    fun onDateUpdated(value: Long?) {
        _uiState.update {
            it.copy(
                tripActivityUiState = it.tripActivityUiState.copy(date = value)
            )
        }
    }

    fun onTimeFromUpdated(value: Pair<Int, Int>) {
        _uiState.update {
            it.copy(
                tripActivityUiState = it.tripActivityUiState.copy(timeFrom = value)
            )
        }
    }

    fun onTimeToUpdated(value: Pair<Int, Int>) {
        _uiState.update {
            it.copy(
                tripActivityUiState = it.tripActivityUiState.copy(timeTo = value)
            )
        }
        checkAllowSaveContent()
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
            deleteTripActivityInfoUseCase.execute(DeleteTripActivityInfoUseCase.Param(activityId)).collect { result ->
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
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_ACTIVITY_INFO)
                }
            }
        }
    }

    fun onDeleteDismiss() {
        _uiState.update {
            it.copy(isShowDeleteConfirmation = false)
        }
    }

    fun onSaveClick() {
        viewModelScope.launch {
            val activityInfo = uiState.value.tripActivityUiState.toActivityInfo()
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
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_ACTIVITY_INFO)
            }
        }
    }

    private suspend fun updateActivityInfo(activityInfo: TripActivityInfo) {
        updateTripActivityInfoUseCase.execute(
            UpdateTripActivityInfoUseCase.Param(tripId, activityInfo)
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
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_ACTIVITY_INFO)
            }
        }
    }

    private fun isUpdateExistingInfo(): Boolean {
        return activityId > 0L
    }

    private fun checkAllowSaveContent() {
        _uiState.update {
            it.copy(allowSaveContent = isAllowSave())
        }
    }

    private fun isAllowSave(): Boolean {
        val isValidName =  uiState.value.tripActivityUiState.name.isNotBlankOrEmpty()
        return isValidName && isValidSchedule()
    }

    private fun isValidSchedule(): Boolean {
        return uiState.value.tripActivityUiState.timeTo.sumAsTotalMinutes() >= uiState.value.tripActivityUiState.timeFrom.sumAsTotalMinutes()
    }

    private fun Pair<Int, Int>.sumAsTotalMinutes(): Int {
        return this.first * 60 + this.second
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

    private fun TripActivityUiState.toActivityInfo(): TripActivityInfo {
        val timeFrom: Long? = if(this.date == null) null else getDateTimeMillis(this.date, this.timeFrom)
        val timeTo: Long? = if(this.date == null) null else getDateTimeMillis(this.date, this.timeTo)

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

    private fun TripActivityInfo.toTripActivityUiState(): TripActivityUiState {
        val timeFrom = if(this.timeFrom != null) dateTimeFormatter.getHourMinute(this.timeFrom) else Pair(0, 0)
        val timeTo = if(this.timeTo != null) dateTimeFormatter.getHourMinute(this.timeTo) else Pair(0, 0)

        return TripActivityUiState(
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
        return dateTimeFormatter.combineHourMinutesDayToMillis(date, time.first, time.second)
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_ADD_ACTIVITY_INFO,
        ERROR_MESSAGE_ACTIVITY_SCHEDULE_IS_NOT_VALID,
        ERROR_MESSAGE_CAN_NOT_LOAD_ACTIVITY_INFO,
        ERROR_MESSAGE_CAN_NOT_UPDATE_ACTIVITY_INFO,
        ERROR_MESSAGE_CAN_NOT_DELETE_ACTIVITY_INFO
    }
}