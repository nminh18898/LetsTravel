package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab

import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfo

data class MemberInfoUiState(
    val memberId: Long,
    val memberName: String,
    val isDefaultBillOwner: Boolean
)

fun MemberInfo.toMemberInfoUiState(): MemberInfoUiState {
    return MemberInfoUiState(
        memberId = memberId,
        memberName = memberName,
        isDefaultBillOwner = false
    )
}