package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member

import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.minhhnn18898.app_navigation.destination.BillSplitManageMemberDestination
import com.minhhnn18898.app_navigation.destination.BillSplitManageMemberDestinationParameters
import com.minhhnn18898.app_navigation.mapper.CustomNavType
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.default_bill_owner.GetTripDefaultBillOwnerStreamUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.default_bill_owner.UpsertNewTripBillOwnerUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.member_info.CreateNewMemberUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.member_info.DeleteMemberUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.member_info.GetAllMembersUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.member_info.UpdateMemberInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.MemberInfoUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main.toMemberInfoUiState
import com.minhhnn18898.trip_data.repo.expense.ReceiptRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

data class BillSplitManageMemberViewUiState(
    val newMemberName: String = "",
    val allowAddNewMember: Boolean = false,
    val isLoading: Boolean = false,
    val deleteMemberUiState: DeleteMemberUiState? = null,
    val updateMemberUiState: UpdateMemberUiState? = null,
    val showError: BillSplitManageMemberViewModel.ErrorType = BillSplitManageMemberViewModel.ErrorType.ERROR_MESSAGE_NONE,
)

data class DeleteMemberUiState(
    val memberId: Long,
    val memberName: String,
    @DrawableRes val memberAvatar: Int
)

data class UpdateMemberUiState(
    val memberId: Long,
    val currentMemberName: String = "",
    val newMemberName: String = "",
    @DrawableRes val memberAvatar: Int,
    val allowUpdateExistingMemberInfo: Boolean = false,
)

@HiltViewModel
class BillSplitManageMemberViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createNewMemberUseCase: CreateNewMemberUseCase,
    getAllMembersUseCase: GetAllMembersUseCase,
    private val updateMemberInfoUseCase: UpdateMemberInfoUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase,
    private val resourceProvider: ManageMemberResourceProvider,
    getTripDefaultBillOwnerStreamUseCase: GetTripDefaultBillOwnerStreamUseCase,
    private val upsertNewTripBillOwnerUseCase: UpsertNewTripBillOwnerUseCase,
    private val repository: ReceiptRepository
): ViewModel() {

    companion object {
        private const val MAX_MEMBER = 30
    }

    private val parameters = savedStateHandle.toRoute<BillSplitManageMemberDestination>(
        typeMap = mapOf(typeOf<BillSplitManageMemberDestinationParameters>() to CustomNavType(BillSplitManageMemberDestinationParameters::class.java, BillSplitManageMemberDestinationParameters.serializer()))
    ).parameters

    val tripId = parameters.tripId

    private val _uiState = MutableStateFlow(BillSplitManageMemberViewUiState())
    val uiState: StateFlow<BillSplitManageMemberViewUiState> = _uiState.asStateFlow()

    val memberInfoContentState: StateFlow<UiState<List<MemberInfoUiState>>> =
        getAllMembersUseCase
            .execute(tripId)
            .combine(getTripDefaultBillOwnerStreamUseCase.execute(tripId)) { memberInfo, defaultBillOwner ->
                UiState.Success(
                    memberInfo.map {
                        it.toMemberInfoUiState(
                            manageMemberResourceProvider = resourceProvider,
                            isDefaultBillOwner = defaultBillOwner?.memberId == it.memberId
                        )
                    }
                )

            }
            .catch<UiState<List<MemberInfoUiState>>> {
                emit(UiState.Error())
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = UiState.Loading
            )

    fun onAddNewMember() {
        val name = uiState.value.newMemberName
        val contentState = memberInfoContentState.value

        if(name.isEmpty() || name.isBlank()) {
            return
        }

        if(contentState is UiState.Success && contentState.data.size >= MAX_MEMBER) {
            showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_LIMIT_MEMBER_REACH)
            return
        }

        addNewMember(name)
    }

    private fun addNewMember(name: String) {
        viewModelScope.launch {
            createNewMemberUseCase.execute(
                tripId = tripId,
                memberName = name,
            ).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                newMemberName = ""
                            )
                        }
                        checkAllowAddNewMember()
                    }
                    is Result.Error ->  showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_ADD_MEMBER)
                }
            }
        }
    }

    fun onNewMemberNameUpdated(value: String) {
        _uiState.update {
            it.copy(
                newMemberName = value,
            )
        }
        checkAllowAddNewMember()
    }

    private fun checkAllowAddNewMember() {
        _uiState.update {
            it.copy(allowAddNewMember = uiState.value.newMemberName.isNotBlankOrEmpty())
        }
    }

    fun onClickUpdateMemberInfo(
        memberId: Long,
        memberName: String,
        memberAvatar: Int,
    ) {
        _uiState.update {
            it.copy(
                updateMemberUiState = UpdateMemberUiState(
                    memberId = memberId,
                    currentMemberName = memberName,
                    newMemberName = memberName,
                    memberAvatar = memberAvatar
                )
            )
        }
    }

    fun onCancelUpdateMemberInfo() {
        _uiState.update {
            it.copy(
                updateMemberUiState = null
            )
        }
    }

    fun onClickDeleteMember(
        memberId: Long,
        memberName: String,
        memberAvatar: Int
    ) {
        _uiState.update {
            it.copy(
                deleteMemberUiState = DeleteMemberUiState(
                    memberId = memberId,
                    memberName = memberName,
                    memberAvatar = memberAvatar
                )
            )
        }
    }

    fun onCancelDeleteMember() {
        _uiState.update {
            it.copy(
                deleteMemberUiState = null
            )
        }
    }

    fun onDeleteMemberConfirmed(memberId: Long) {
        viewModelScope.launch {
            deleteMemberUseCase.execute(tripId, memberId).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                deleteMemberUiState = null,
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                deleteMemberUiState = null,
                            )
                        }
                        showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_MEMBER)
                    }
                }
            }
        }
    }

    fun onExistingMemberNameUpdated(value: String) {
        _uiState.update { state ->
            state.copy(
                updateMemberUiState = state.updateMemberUiState?.copy(newMemberName = value)
            )
        }
        checkAllowUpdateExistingMember()
    }

    private fun checkAllowUpdateExistingMember() {
        _uiState.update {
            val updateMemberState = it.updateMemberUiState
            it.copy(
                updateMemberUiState = updateMemberState?.copy(
                    allowUpdateExistingMemberInfo = updateMemberState.newMemberName.isNotBlankOrEmpty()
                )
            )
        }
    }

    fun onUpdateMemberInfo(memberId: Long) {
        val memberName = _uiState.value.updateMemberUiState?.newMemberName ?: ""

        if(memberName.isEmpty()) {
            showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_MEMBER)
            return
        }

        viewModelScope.launch {
            updateMemberInfoUseCase.execute(
                tripId = tripId,
                memberId = memberId,
                memberName = memberName
            ).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                updateMemberUiState = null,
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                updateMemberUiState = null,
                            )
                        }
                        showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_MEMBER)
                    }
                }
            }
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

    fun onUpdateDefaultBillOwner(memberId: Long) {
        if(memberInfoContentState.isAlreadyDefaultBillOwner(memberId)) {
            return
        }

        _uiState.update {
            it.copy(
                isLoading = true,
            )
        }

        viewModelScope.launch {
            upsertNewTripBillOwnerUseCase.execute(
                tripId = tripId,
                memberId = memberId,
            ).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                    }
                    is Result.Error -> {
                        _uiState.update { it.copy(isLoading = false) }
                        showErrorInBriefPeriod(ErrorType.ERROR_CAN_NOT_UPDATE_BILL_OWNER)
                    }
                }
            }
        }
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_LIMIT_MEMBER_REACH,
        ERROR_MESSAGE_CAN_NOT_ADD_MEMBER,
        ERROR_MESSAGE_CAN_NOT_UPDATE_MEMBER,
        ERROR_MESSAGE_CAN_NOT_DELETE_MEMBER,
        ERROR_CAN_NOT_UPDATE_BILL_OWNER
    }
}

private fun StateFlow<UiState<List<MemberInfoUiState>>>.isAlreadyDefaultBillOwner(memberId: Long): Boolean {
    return (this.value as? UiState.Success)
        ?.data
        ?.firstOrNull { it.isDefaultBillOwner }
        ?.memberId == memberId
}