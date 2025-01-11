package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main

import android.util.Log
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.manage_trip.trip_detail.domain.member_info.GetAllMembersUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.receipt.GetAllReceiptsUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProvider
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TripDetailExpenseTabController(
    viewModelScope: CoroutineScope,
    tripId: Long,
    getAllMembersUseCase: GetAllMembersUseCase,
    private val memberResourceProvider: ManageMemberResourceProvider,
    private val dateTimeFormatter: TripDetailDateTimeFormatter,
    getAllReceiptsUseCase: GetAllReceiptsUseCase
) {

    val memberInfoContentState: StateFlow<UiState<List<MemberInfoUiState>>> =
        getAllMembersUseCase
            .execute(tripId)
            .map { memberInfo ->
                UiState.Success(
                    memberInfo.map {
                        it.toMemberInfoUiState(
                            manageMemberResourceProvider = memberResourceProvider,
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

    val receiptInfoContentState: StateFlow<UiState<List<ReceiptWithAllPayersInfoUiState>>> =
        getAllReceiptsUseCase
            .execute(tripId)
            .map { receiptInfo ->
                UiState.Success(
                    receiptInfo.map {
                        it.toReceiptWithAllPayersInfoUiState(
                            manageMemberResourceProvider = memberResourceProvider,
                            dateTimeFormatter = dateTimeFormatter
                        )
                    }
                )
            }
            .catch<UiState<List<ReceiptWithAllPayersInfoUiState>>> {
                Log.e("TestException", it.stackTrace.toString())
                emit(UiState.Error())
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = UiState.Loading
            )
}