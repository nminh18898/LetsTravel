package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.minhhnn18898.app_navigation.destination.TripDetailDestination
import com.minhhnn18898.app_navigation.destination.TripDetailDestinationParameters
import com.minhhnn18898.app_navigation.destination.TripDetailPlanTabDestination
import com.minhhnn18898.app_navigation.destination.TripDetailTabDestination
import com.minhhnn18898.app_navigation.mapper.CustomNavType
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.GetSortedListTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.GetListFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.hotel.GetListHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.GetAllMembersUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.GetMemberReceiptPaymentStatisticInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.memories_config.GetMemoriesConfigUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.memories_config.UpdateMemoriesConfigUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.photo.AddTripPhotoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.photo.DeleteTripPhotoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.photo.GetAllPhotoFrameTypeUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.photo.GetAllTripPhotosUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.receipt.GetAllReceiptsUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.TripDetailExpenseTabController
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.BillSplitManageMemberViewModel.ErrorType
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProvider
import com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab.MemoriesTabController
import com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab.MemoriesTabResourceProvider
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.main.TripDetailPlanTabController
import com.minhhnn18898.manage_trip.trip_info.domain.GetTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ICoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ITripActivityDateSeparatorResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.UserTripDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.toTripItemDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

data class TripDetailScreenTripInfoUiState(
    val tripDisplay: UserTripDisplay? = null,
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false
)

data class TripDetailMainUiState(
    val isLoading: Boolean = false,
    val showError: TripDetailScreenViewModel.ErrorType = TripDetailScreenViewModel.ErrorType.ERROR_MESSAGE_NONE
)

@HiltViewModel
class TripDetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val coverResourceProvider: ICoverDefaultResourceProvider,
    activityDateSeparatorResourceProvider: ITripActivityDateSeparatorResourceProvider,
    getTripInfoUseCase: GetTripInfoUseCase,
    getListFlightInfoUseCase: GetListFlightInfoUseCase,
    getListHotelInfoUseCase: GetListHotelInfoUseCase,
    getSortedListTripActivityInfoUseCase: GetSortedListTripActivityInfoUseCase,
    dateTimeFormatter: TripDetailDateTimeFormatter,
    getAllMembersUseCase: GetAllMembersUseCase,
    memberResourceProvider: ManageMemberResourceProvider,
    getAllReceiptsUseCase: GetAllReceiptsUseCase,
    getMemberPaymentStatisticInfo: GetMemberReceiptPaymentStatisticInfo,
    getAllTripPhotosUseCase: GetAllTripPhotosUseCase,
    private val addTripPhotoUseCase: AddTripPhotoUseCase,
    private val removeTripPhotoUseCase: DeleteTripPhotoUseCase,
    getMemoriesConfigUseCase: GetMemoriesConfigUseCase,
    getAllPhotoFrameTypeUseCase: GetAllPhotoFrameTypeUseCase,
    memoriesTabResourceProvider: MemoriesTabResourceProvider,
    updateMemoriesConfigUseCase: UpdateMemoriesConfigUseCase
): ViewModel() {

    private val parameters = savedStateHandle.toRoute<TripDetailDestination>(
        typeMap = mapOf(typeOf<TripDetailDestinationParameters>() to CustomNavType(TripDetailDestinationParameters::class.java, TripDetailDestinationParameters.serializer()))
    ).parameters

    val tripId = parameters.tripId

    val tripInfoContentState: StateFlow<TripDetailScreenTripInfoUiState> =
        getTripInfoUseCase.execute(GetTripInfoUseCase.Param(tripId)).map {
            if(it != null) {
                TripDetailScreenTripInfoUiState(
                    tripDisplay = it.toTripItemDisplay(defaultCoverResourceProvider = coverResourceProvider),
                    isLoading = false
                )
            } else {
                TripDetailScreenTripInfoUiState(
                    isLoading = false,
                    isNotFound = true
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = TripDetailScreenTripInfoUiState(isLoading = true)
        )

    var tripName: String = ""
        private set
        get() = tripInfoContentState.value.tripDisplay?.tripName ?: ""


    val planTabUIController = TripDetailPlanTabController(
        viewModelScope = viewModelScope,
        tripId = tripId,
        activityDateSeparatorResourceProvider = activityDateSeparatorResourceProvider,
        dateTimeFormatter = dateTimeFormatter,
        getListFlightInfoUseCase = getListFlightInfoUseCase,
        getListHotelInfoUseCase = getListHotelInfoUseCase,
        getSortedListTripActivityInfoUseCase = getSortedListTripActivityInfoUseCase
    )

    val expenseTabController = TripDetailExpenseTabController(
        viewModelScope = viewModelScope,
        tripId = tripId,
        getAllMembersUseCase = getAllMembersUseCase,
        memberResourceProvider = memberResourceProvider,
        getAllReceiptsUseCase = getAllReceiptsUseCase,
        dateTimeFormatter = dateTimeFormatter,
        getMemberReceiptPaymentStatisticInfo = getMemberPaymentStatisticInfo
    )

    val memoriesTabController = MemoriesTabController(
        viewModelScope = viewModelScope,
        tripId = tripId,
        getAllTripPhotosUseCase = getAllTripPhotosUseCase,
        getMemoriesConfigUseCase = getMemoriesConfigUseCase,
        getAllPhotoFrameTypeUseCase = getAllPhotoFrameTypeUseCase,
        resourceProvider = memoriesTabResourceProvider,
        updateMemoriesConfigUseCase = updateMemoriesConfigUseCase
    )

    private val _uiState = MutableStateFlow(TripDetailMainUiState())
    val uiState: StateFlow<TripDetailMainUiState> = _uiState.asStateFlow()

    var currentTabSelected: TripDetailTabDestination by mutableStateOf(TripDetailPlanTabDestination)
        private set

    fun onChangeTab(tab: TripDetailTabDestination) {
        currentTabSelected = tab
    }

    fun onAddTripPhoto(uris: List<Uri>) {
        viewModelScope.launch {
            addTripPhotoUseCase.execute(
                tripId = tripId,
                uris = uris,
            ).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> _uiState.update { it.copy(isLoading = false) }
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_PHOTO)
                }
            }
        }
    }

    fun onRemovePhoto(photoId: Long) {
        viewModelScope.launch {
            removeTripPhotoUseCase.execute(photoId).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> _uiState.update { it.copy(isLoading = false) }
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_PHOTO)
                }
            }
        }
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_ADD_PHOTO,
        ERROR_MESSAGE_CAN_NOT_DELETE_PHOTO
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
}

