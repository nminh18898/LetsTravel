package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab

import androidx.annotation.DrawableRes
import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProvider

data class MemberInfoUiState(
    val memberId: Long,
    val memberName: String,
    @DrawableRes val avatarRes: Int,
    val isDefaultBillOwner: Boolean
)

fun MemberInfo.toMemberInfoUiState(
    manageMemberResourceProvider: ManageMemberResourceProvider,
    isDefaultBillOwner: Boolean = false
): MemberInfoUiState {
    return MemberInfoUiState(
        memberId = memberId,
        memberName = memberName,
        avatarRes = manageMemberResourceProvider.getAvatarResource(avatarId),
        isDefaultBillOwner = isDefaultBillOwner
    )
}