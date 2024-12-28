package com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.hotel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.hotel.CreateNewHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.hotel.DeleteHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.hotel.GetHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.hotel.UpdateHotelInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HotelUiState(
    val hotelName: String = "",
    val address: String = "",
    val prices: String = "",
    val checkInDate: Long? = null,
    val checkOutDate: Long? = null
)

data class AddEditHotelUiState(
    val hotelUiState: HotelUiState = HotelUiState(),
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false,
    val canDelete: Boolean = false,
    val isShowDeleteConfirmation: Boolean = false,
    val showError: AddEditHotelInfoViewModel.ErrorType = AddEditHotelInfoViewModel.ErrorType.ERROR_MESSAGE_NONE,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val allowSaveContent: Boolean = false
)

@HiltViewModel
class AddEditHotelInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createNewHotelInfoUseCase: CreateNewHotelInfoUseCase,
    private val getHotelInfoUseCase: GetHotelInfoUseCase,
    private val updateHotelInfoUseCase: UpdateHotelInfoUseCase,
    private val deleteHotelInfoUseCase: DeleteHotelInfoUseCase
): ViewModel() {

    private var tripId: Long = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1
    private var hotelId: Long = savedStateHandle.get<Long>(MainAppRoute.hotelIdArg) ?: 0L

    private val _uiState = MutableStateFlow(AddEditHotelUiState())
    val uiState: StateFlow<AddEditHotelUiState> = _uiState.asStateFlow()

    init {
        if(hotelId > 0) {
            loadHotelInfo(hotelId = hotelId)
        }
    }

    private fun loadHotelInfo(hotelId: Long) {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getHotelInfoUseCase.execute(GetHotelInfoUseCase.Param(hotelId)).collect { hotelInfo ->
                if(hotelInfo != null) {
                    _uiState.update {
                        it.copy(
                            hotelUiState = hotelInfo.toEditHotelUiState(),
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

    fun onHotelNameUpdated(hotelName: String) {
        _uiState.update {
            it.copy(
                hotelUiState = it.hotelUiState.copy(hotelName = hotelName)
            )
        }
        checkAllowSaveContent()
    }

    fun onAddressUpdated(address: String) {
        _uiState.update {
            it.copy(
                hotelUiState = it.hotelUiState.copy(address = address)
            )
        }
    }

    fun onPricesUpdated(prices: String) {
        _uiState.update {
            it.copy(
                hotelUiState = it.hotelUiState.copy(prices = prices)
            )
        }
    }

    fun onCheckInDateUpdated(checkInDate: Long?) {
        _uiState.update {
            it.copy(
                hotelUiState = it.hotelUiState.copy(checkInDate = checkInDate)
            )
        }
        checkAllowSaveContent()
    }

    fun onCheckOutDateUpdated(checkOutDate: Long?) {
        _uiState.update {
            it.copy(
                hotelUiState = it.hotelUiState.copy(checkOutDate = checkOutDate)
            )
        }
        checkInvalidHotelCheckOutTimeRangeAndNotify()
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
            deleteHotelInfoUseCase.execute(DeleteHotelInfoUseCase.Param(hotelId)).collect { result ->
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
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_HOTEL_INFO)
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
            val hotelInfo = uiState.value.hotelUiState.toHotelInfo()
            if(isUpdateExistingInfo()) {
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
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_HOTEL_INFO)
            }
        }
    }

    private suspend fun updateHotelInfo(hotelInfo: HotelInfo) {
        updateHotelInfoUseCase.execute(
            UpdateHotelInfoUseCase.Param(tripId, hotelInfo)
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
                is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_HOTEL_INFO)
            }
        }
    }

    private fun isUpdateExistingInfo(): Boolean {
        return hotelId > 0L
    }

    private fun checkInvalidHotelCheckOutTimeRangeAndNotify() {
        if(!isValidDateTime()) {
            showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_STAY_DURATION_IS_NOT_VALID)
        }
    }

    private fun checkAllowSaveContent() {
        _uiState.update {
            it.copy(allowSaveContent = isAllowSave())
        }
    }

    private fun isAllowSave(): Boolean {
        val isValidName =  uiState.value.hotelUiState.hotelName.isNotBlankOrEmpty()
        return isValidName && isValidDateTime()
    }

    private fun isValidDateTime(): Boolean {
        val checkInDate = uiState.value.hotelUiState.checkInDate ?: return false
        val checkOutDate = uiState.value.hotelUiState.checkOutDate ?: return false
        return checkOutDate > checkInDate
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

    private fun HotelUiState.toHotelInfo(): HotelInfo {
        return HotelInfo(
            hotelId = hotelId,
            hotelName = this.hotelName,
            address = this.address,
            price = this.prices.toLongOrNull() ?: 0L,
            checkInDate = this.checkInDate ?: 0L,
            checkOutDate = this.checkOutDate ?: 0L
        )
    }

    private fun HotelInfo.toEditHotelUiState(): HotelUiState {
        return HotelUiState(
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
}