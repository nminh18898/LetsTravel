package com.minhhnn18898.letstravel.tripdetail.ui.hotel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.letstravel.tripdetail.data.model.HotelInfo
import com.minhhnn18898.letstravel.tripdetail.usecase.CreateNewHotelInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.usecase.DeleteHotelInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.usecase.GetHotelInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.usecase.UpdateHotelInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditHotelInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createNewHotelInfoUseCase: CreateNewHotelInfoUseCase,
    private val getHotelInfoUseCase: GetHotelInfoUseCase,
    private val updateHotelInfoUseCase: UpdateHotelInfoUseCase,
    private val deleteHotelInfoUseCase: DeleteHotelInfoUseCase
): ViewModel() {

    private var tripId: Long = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1

    private var hotelId: Long = savedStateHandle.get<Long>(MainAppRoute.hotelIdArg) ?: 0L

    var canDeleteInfo by mutableStateOf(hotelId > 0L)

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

    var uiState = mutableStateOf(EditHotelUiState())
        private set

    init {
        loadHotelInfo(hotelId = hotelId)
    }

    private fun loadHotelInfo(hotelId: Long) {
        if(hotelId <= 0 ) return

        viewModelScope.launch {
            getHotelInfoUseCase.execute(GetHotelInfoUseCase.Param(hotelId))?.collect {
                onShowLoadingState = it == Result.Loading

                when(it) {
                    is Result.Success -> {
                        uiState.value = it.data.toEditHotelUiState()
                        checkAllowSaveContent()
                    }
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_LOAD_HOTEL_INFO)
                    else -> {
                        // do nothing
                    }
                }
            }
        }

    }

    fun onHotelNameUpdated(hotelName: String) {
        uiState.value = uiState.value.copy(hotelName = hotelName)
        checkAllowSaveContent()
    }

    fun onAddressUpdated(address: String) {
        uiState.value = uiState.value.copy(address = address)
    }

    fun onPricesUpdated(prices: String) {
        uiState.value = uiState.value.copy(prices = prices)
    }

    fun onCheckInDateUpdated(checkInDate: Long?) {
        uiState.value = uiState.value.copy(checkInDate = checkInDate)
        checkAllowSaveContent()
    }

    fun onCheckOutDateUpdated(checkOutDate: Long?) {
        uiState.value = uiState.value.copy(checkOutDate = checkOutDate)
        checkInvalidHotelCheckOutTimeRangeAndNotify()
        checkAllowSaveContent()
    }

    fun onDeleteClick() {
        onShowDialogDeleteConfirmation = true
    }

    fun onDeleteConfirm() {
        onShowDialogDeleteConfirmation = false

        viewModelScope.launch {
            deleteHotelInfoUseCase.execute(DeleteHotelInfoUseCase.Param(hotelId))?.collect {
                onShowLoadingState = it == Result.Loading

                when(it) {
                    is Result.Success -> _eventChannel.send(Event.CloseScreen)
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_HOTEL_INFO)
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
            val hotelInfo = uiState.value.toHotelInfo()
            if(isEditExistingInfo()) {
                updateHotelInfo(hotelInfo)
            }
            else {
                createNewHotelInfo(hotelInfo)
            }
        }
    }

    private suspend fun createNewHotelInfo(hotelInfo: HotelInfo) {
        createNewHotelInfoUseCase.execute(
            CreateNewHotelInfoUseCase.Param(tripId, hotelInfo)
        )?.collect {
            onShowLoadingState = it == Result.Loading

            when(it) {
                is Result.Success -> _eventChannel.send(Event.CloseScreen)
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_HOTEL_INFO)
                else -> {
                    // do nothing
                }
            }
        }
    }

    private suspend fun updateHotelInfo(hotelInfo: HotelInfo) {
        updateHotelInfoUseCase.execute(
            UpdateHotelInfoUseCase.Param(tripId, hotelInfo)
        )?.collect {
            onShowLoadingState = it == Result.Loading

            when(it) {
                is Result.Success -> _eventChannel.send(Event.CloseScreen)
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_HOTEL_INFO)
                else -> {
                    // do nothing
                }
            }
        }
    }

    private fun isEditExistingInfo(): Boolean {
        return hotelId > 0L
    }

    private fun checkInvalidHotelCheckOutTimeRangeAndNotify() {
        if(!isValidDateTime()) {
            showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_STAY_DURATION_IS_NOT_VALID)
        }
    }

    private fun checkAllowSaveContent() {
        val isValidHotelName =  uiState.value.hotelName.isNotBlankOrEmpty()
        allowSaveContent = isValidHotelName && isValidDateTime()
    }

    private fun isValidDateTime(): Boolean {
        val checkInDate = uiState.value.checkInDate ?: return false
        val checkOutDate = uiState.value.checkOutDate ?: return false
        return checkOutDate > checkInDate
    }

    @Suppress("SameParameterValue")
    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            this@EditHotelInfoViewModel.errorType = errorType
            delay(3000)
            this@EditHotelInfoViewModel.errorType = ErrorType.ERROR_MESSAGE_NONE
        }
    }

    data class EditHotelUiState(
        val hotelName: String = "",
        val address: String = "",
        val prices: String = "",
        val checkInDate: Long? = null,
        val checkOutDate: Long? = null
    )

    private fun EditHotelUiState.toHotelInfo(): HotelInfo {
        return HotelInfo(
            hotelId = hotelId,
            hotelName = this.hotelName,
            address = this.address,
            price = this.prices.toLongOrNull() ?: 0L,
            checkInDate = this.checkInDate ?: 0L,
            checkOutDate = this.checkOutDate ?: 0L
        )
    }

    private fun HotelInfo.toEditHotelUiState(): EditHotelUiState {
        return EditHotelUiState(
            hotelName = this.hotelName,
            address = this.address,
            prices = this.price.toString(),
            checkInDate = this.checkInDate,
            checkOutDate = this.checkOutDate
        )
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_ADD_HOTEL_INFO,
        ERROR_MESSAGE_STAY_DURATION_IS_NOT_VALID,
        ERROR_MESSAGE_CAN_NOT_LOAD_HOTEL_INFO,
        ERROR_MESSAGE_CAN_NOT_UPDATE_HOTEL_INFO,
        ERROR_MESSAGE_CAN_NOT_DELETE_HOTEL_INFO
    }

    sealed class Event {
        data object CloseScreen: Event()
    }
}