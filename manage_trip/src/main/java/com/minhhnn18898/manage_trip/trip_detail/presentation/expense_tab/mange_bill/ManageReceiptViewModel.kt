package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.mange_bill

import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.minhhnn18898.app_navigation.destination.ManageBillDestination
import com.minhhnn18898.app_navigation.destination.ManageBillDestinationParameters
import com.minhhnn18898.app_navigation.mapper.CustomNavType
import com.minhhnn18898.core.utils.DateTimeProvider
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.manage_trip.trip_detail.domain.default_bill_owner.GetTripDefaultBillOwnerStreamUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.GetAllMembersUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.MemberInfoSelectionUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.MemberInfoUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.toMemberInfoUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProvider
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

data class ReceiptUiState(
    val name: String = "",
    val description: String = "",
    val prices: String = "",
    val formattedDate: String = "",
    val dateCreated: Long? = null,
    val timeCreated: Pair<Int, Int> = Pair(0, 0),
    @DrawableRes val receiptOwnerAvatar: Int = 0
)

data class UpdateReceiptOwnerUiState(
    val listMemberReceiptOwnerSelection: List<MemberInfoSelectionUiState> = emptyList()
)

data class ManageReceiptUiState(
    val receiptUiState: ReceiptUiState = ReceiptUiState(),
    val updateReceiptOwnerUiState: UpdateReceiptOwnerUiState = UpdateReceiptOwnerUiState(),
    val receiptOwner: MemberInfoUiState? = null,
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false,
    val canDelete: Boolean = false,
    val isShowDeleteConfirmation: Boolean = false,
    val showError: ManageReceiptViewModel.ErrorType = ManageReceiptViewModel.ErrorType.ERROR_MESSAGE_NONE,
    val isCreated: Boolean = false,
    val isUpdated: Boolean = false,
    val isDeleted: Boolean = false,
    val allowSaveContent: Boolean = false
)

@HiltViewModel
class ManageReceiptViewModel@Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val resourceProvider: ManageMemberResourceProvider,
    private val getAllMembersUseCase: GetAllMembersUseCase,
    private val getTripDefaultBillOwnerStreamUseCase: GetTripDefaultBillOwnerStreamUseCase,
    private val dateTimeFormatter: TripDetailDateTimeFormatter,
    private val dateTimeProvider: DateTimeProvider
): ViewModel() {

    private val parameters = savedStateHandle.toRoute<ManageBillDestination>(
        typeMap = mapOf(typeOf<ManageBillDestinationParameters>() to CustomNavType(ManageBillDestinationParameters::class.java, ManageBillDestinationParameters.serializer()))
    ).parameters

    val tripId = parameters.tripId
    val receiptId = parameters.receiptId

    private val _uiState = MutableStateFlow(
        ManageReceiptUiState(
            receiptUiState = ReceiptUiState(
                formattedDate = dateTimeFormatter.getFormattedReceiptCreatedDate(dateTimeProvider.currentTimeMillis())
            )
        )
    )
    val uiState: StateFlow<ManageReceiptUiState> = _uiState.asStateFlow()

    init {
        loadListMemberInfo()
    }

    private fun checkAllowSaveContent() {
        _uiState.update {
            it.copy(allowSaveContent = isAllowSave())
        }
    }

    private fun isAllowSave(): Boolean {
        return uiState.value.receiptUiState.name.isNotBlankOrEmpty()
                && uiState.value.receiptUiState.prices.isNotBlankOrEmpty()
    }

    fun onNameUpdated(value: String) {
        _uiState.update {
            it.copy(
                receiptUiState = it.receiptUiState.copy(name = value)
            )
        }
        checkAllowSaveContent()
    }

    fun onDescriptionUpdated(value: String) {
        _uiState.update {
            it.copy(
                receiptUiState = it.receiptUiState.copy(description = value)
            )
        }
    }

    fun onPricesUpdated(value: String) {
        _uiState.update {
            it.copy(
                receiptUiState = it.receiptUiState.copy(prices = value)
            )
        }
        checkAllowSaveContent()
    }

    fun onDateUpdated(value: Long?) {
        _uiState.update { state ->
            state.copy(
                receiptUiState = state.receiptUiState.copy(
                    dateCreated = value,
                    formattedDate = dateTimeFormatter.getFormattedReceiptCreatedDate(
                        getDateTimeMillis(
                            date = value ?: dateTimeProvider.currentTimeMillis(),
                            time = state.receiptUiState.timeCreated
                        )
                    )
                )
            )
        }
    }

    fun onTimeUpdated(value: Pair<Int, Int>) {
        _uiState.update { state ->
            state.copy(
                receiptUiState = state.receiptUiState.copy(
                    timeCreated = value,
                    formattedDate = dateTimeFormatter.getFormattedReceiptCreatedDate(
                        getDateTimeMillis(
                            date = state.receiptUiState.dateCreated ?: dateTimeProvider.currentTimeMillis(),
                            time = value
                        )
                    )
                )
            )
        }
    }

    private fun getDateTimeMillis(date: Long, time: Pair<Int, Int>): Long {
        return dateTimeFormatter.combineHourMinutesDayToMillis(date, time.first, time.second)
    }

    fun onSelectNewReceiptOwner(member: MemberInfoUiState) {
        val currentUiState = uiState.value.updateReceiptOwnerUiState
        val updatedList = currentUiState.listMemberReceiptOwnerSelection.map {
            it.copy(isSelected = it.memberInfo.memberId == member.memberId)
        }

        val updatedReceiptOwnerUiState = currentUiState.copy(
            listMemberReceiptOwnerSelection = updatedList
        )

        _uiState.update { state ->
            state.copy(
                updateReceiptOwnerUiState = updatedReceiptOwnerUiState,
                receiptOwner = member
            )
        }
    }

    private fun loadListMemberInfo() {
        viewModelScope.launch {
            getAllMembersUseCase
                .execute(tripId)
                .combine(getTripDefaultBillOwnerStreamUseCase.execute(tripId)) { memberInfo, defaultBillOwner ->
                    Pair(memberInfo, defaultBillOwner)
                }
                .collect { (memberInfo, defaultBillOwner) ->
                    _uiState.update { state ->
                        val listMember = memberInfo.map {
                            it.toMemberInfoUiState(
                                manageMemberResourceProvider = resourceProvider,
                                isDefaultBillOwner = defaultBillOwner?.memberId == it.memberId
                            )
                        }

                        state.copy(
                            updateReceiptOwnerUiState = UpdateReceiptOwnerUiState(
                                listMemberReceiptOwnerSelection = listMember.map {
                                    MemberInfoSelectionUiState(
                                        memberInfo = it,
                                        isSelected = it.isDefaultBillOwner
                                    )
                                }
                            ),
                            receiptOwner = listMember.firstOrNull { it.isDefaultBillOwner }
                        )
                    }
                }
        }
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_CREATE_RECEIPT,
        ERROR_MESSAGE_CAN_NOT_UPDATE_RECEIPT,
        ERROR_MESSAGE_CAN_NOT_DELETE_RECEIPT,
        ERROR_MESSAGE_CAN_NOT_UPDATE_RECEIPT_OWNER
    }
}

