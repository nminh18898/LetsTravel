package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main

import androidx.annotation.DrawableRes
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptWithAllPayersInfo
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

data class ReceiptWithAllPayersInfoUiState(
    val receiptInfo: ReceiptInfoUiState,
    val receiptOwner: MemberInfoUiState,
    val receiptPayers: List<ReceiptPayerInfoUiState>
)

fun ReceiptWithAllPayersInfo.toReceiptWithAllPayersInfoUiState(
    manageMemberResourceProvider: ManageMemberResourceProvider
): ReceiptWithAllPayersInfoUiState {
    return ReceiptWithAllPayersInfoUiState(
        receiptInfo = receiptInfo.toReceiptInfoUiState(),
        receiptOwner = receiptOwner.toMemberInfoUiState(manageMemberResourceProvider),
        receiptPayers = receiptPayers.map {
            it.toReceiptPayerInfoUiState(manageMemberResourceProvider)
        }
    )
}

data class ReceiptInfoUiState(
    val receiptId: Long = 0,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val createdTime: String = "",
    val splittingMode: Int = 0
)

fun ReceiptInfo.toReceiptInfoUiState(): ReceiptInfoUiState {
    return ReceiptInfoUiState(
        receiptId = this.receiptId,
        name = this.name,
        description = this.description,
        price = this.price.toString(),
        createdTime = this.createdTime.toString(),
        splittingMode = this.splittingMode
    )
}

data class ReceiptPayerInfoUiState(
    val memberId: Long = 0,
    val memberName: String = "",
    @DrawableRes val avatarRes: Int,
    val payAmount: String = ""
)

fun ReceiptPayerInfo.toReceiptPayerInfoUiState(manageMemberResourceProvider: ManageMemberResourceProvider): ReceiptPayerInfoUiState {
    return ReceiptPayerInfoUiState(
        memberId = memberId,
        memberName = memberName,
        avatarRes = manageMemberResourceProvider.getAvatarResource(memberAvatar),
        payAmount = payAmount.toString()
    )
}