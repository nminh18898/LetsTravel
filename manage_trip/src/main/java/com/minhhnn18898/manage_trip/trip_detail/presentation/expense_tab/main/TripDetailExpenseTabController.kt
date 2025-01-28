package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main

import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptWithAllPayersInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.member_info.GetAllMembersUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.member_info.GetMemberReceiptPaymentStatisticInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.receipt.GetAllReceiptsUseCase
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
    getAllReceiptsUseCase: GetAllReceiptsUseCase,
    getMemberReceiptPaymentStatisticInfo: GetMemberReceiptPaymentStatisticInfo
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

    val memberPaymentStatisticContent: StateFlow<UiState<List<MemberReceiptPaymentStatisticUiState>>> =
        getMemberReceiptPaymentStatisticInfo
            .execute(tripId)
            .map { memberPaymentInfo ->
                UiState.Success(
                    memberPaymentInfo.map {
                        it.toMemberReceiptPaymentStatisticUiState()
                    }
                )
            }
            .catch<UiState<List<MemberReceiptPaymentStatisticUiState>>> {
                emit(UiState.Error())
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = UiState.Loading
            )

    val receiptInfoContentState: StateFlow<UiState<List<ReceiptWithAllPayersInfoItemDisplay>>> =
        getAllReceiptsUseCase
            .execute(tripId)
            .map {
                UiState.Success(it.makeListReceiptInfoDisplay())
            }
            .catch<UiState<List<ReceiptWithAllPayersInfoItemDisplay>>> {
                emit(UiState.Error())
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = UiState.Loading
            )

    private fun Map<Long, List<ReceiptWithAllPayersInfo>>.makeListReceiptInfoDisplay(): List<ReceiptWithAllPayersInfoItemDisplay> {
        val itemRender = mutableListOf<ReceiptWithAllPayersInfoItemDisplay>()

        this.forEach { (date, receiptInfo) ->
            itemRender.add(ReceiptWithAllPayersInfoDateSeparatorUiState(description = dateTimeFormatter.getReceiptFormattedDateSeparatorString(date)))
            itemRender.addAll(
                receiptInfo.map {
                    it.toReceiptWithAllPayersInfoUiState(
                        manageMemberResourceProvider = memberResourceProvider,
                        dateTimeFormatter = dateTimeFormatter
                    )
                }
            )
        }

        return itemRender
    }
}