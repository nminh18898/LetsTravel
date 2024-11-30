package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member

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
import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.CreateNewMemberUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.DeleteMemberUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.GetAllMembersUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.UpdateMemberInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.MemberInfoUiState
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.toMemberInfoUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

data class BillSplitManageMemberViewUiState(
    val newMemberName: String = "",
    val isLoading: Boolean = false,
    val allowAddNewMember: Boolean = false,
    val isShowDeleteMemberConfirmation: Boolean = false,
    val isDisplayUpdateMemberInfoForm: Boolean = false,
    val showError: BillSplitManageMemberViewModel.ErrorType = BillSplitManageMemberViewModel.ErrorType.ERROR_MESSAGE_NONE,
)

@HiltViewModel
class BillSplitManageMemberViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createNewMemberUseCase: CreateNewMemberUseCase,
    getAllMembersUseCase: GetAllMembersUseCase,
    private val updateMemberInfoUseCase: UpdateMemberInfoUseCase,
    private val deleteMemberUseCase: DeleteMemberUseCase
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
        getAllMembersUseCase.execute(tripId)
            .map { memberInfo ->
                UiState.Success(memberInfo.map { it.toMemberInfoUiState() })
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
                memberInfo = MemberInfo(
                    memberId = 0L,
                    memberName = name
                )
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

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_LIMIT_MEMBER_REACH,
        ERROR_MESSAGE_CAN_NOT_ADD_MEMBER,
        ERROR_MESSAGE_CAN_NOT_UPDATE_MEMBER,
        ERROR_MESSAGE_CAN_NOT_DELETE_MEMBER
    }
}