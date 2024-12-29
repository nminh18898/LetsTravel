package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main

import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.GetAllMembersUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TripDetailExpenseTabController(
    viewModelScope: CoroutineScope,
    tripId: Long,
    getAllMembersUseCase: GetAllMembersUseCase,
    private val memberResourceProvider: ManageMemberResourceProvider
) {

    val memberInfoContentState: StateFlow<UiState<List<MemberInfoUiState>>> =
        getAllMembersUseCase
            .execute(tripId)
            .map { memberInfo ->
                UiState.Success(
                    memberInfo.map {
                        it.toMemberInfoUiState(
                            manageMemberResourceProvider = memberResourceProvider,
                            // Suppress display of bill owner state
                            isDefaultBillOwner = false
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

}