package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main

import androidx.annotation.DrawableRes
import com.minhhnn18898.core.utils.formatWithCommas
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptWithAllPayersInfo
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProvider
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter

data class MemberInfoUiState(
    val memberId: Long = 0,
    val memberName: String = "",
    @DrawableRes val avatarRes: Int = 0,
    val isDefaultBillOwner: Boolean = false
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
    manageMemberResourceProvider: ManageMemberResourceProvider,
    dateTimeFormatter: TripDetailDateTimeFormatter
): ReceiptWithAllPayersInfoUiState {
    val minAmount = receiptPayers.minOfOrNull { it.payAmount } ?: 0L
    val maxAmount = receiptPayers.maxOfOrNull { it.payAmount } ?: 0L
    val amountPerPerson = when {
        minAmount == maxAmount && minAmount == 0L -> ""
        minAmount == maxAmount -> minAmount.formatWithCommas()
        else -> "${minAmount.formatWithCommas()}-${maxAmount.formatWithCommas()}"
    }

    return ReceiptWithAllPayersInfoUiState(
        receiptInfo = receiptInfo.toReceiptInfoUiState(amountPerPerson, dateTimeFormatter),
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
    val pricePerPersonDescription: String= ""
)

fun ReceiptInfo.toReceiptInfoUiState(
    pricePerPersonDescription: String,
    dateTimeFormatter: TripDetailDateTimeFormatter
): ReceiptInfoUiState {
    return ReceiptInfoUiState(
        receiptId = this.receiptId,
        name = this.name,
        description = this.description,
        price = this.price.formatWithCommas(),
        createdTime = dateTimeFormatter.getFormattedReceiptCreatedDate(this.createdTime),
        pricePerPersonDescription = pricePerPersonDescription
    )
}

data class ReceiptPayerInfoUiState(
    val memberInfo: MemberInfoUiState = MemberInfoUiState(),
    val payAmount: String = ""
)

fun ReceiptPayerInfoUiState.toReceiptPayerInfo(): ReceiptPayerInfo {
    return ReceiptPayerInfo(
        memberId = this.memberInfo.memberId,
        memberName = this.memberInfo.memberName,
        payAmount = this.payAmount.toLongOrNull() ?: 0L
    )
}

fun ReceiptPayerInfo.toReceiptPayerInfoUiState(manageMemberResourceProvider: ManageMemberResourceProvider): ReceiptPayerInfoUiState {
    return ReceiptPayerInfoUiState(
        memberInfo = MemberInfoUiState(
            memberId = memberId,
            memberName = memberName,
            avatarRes = manageMemberResourceProvider.getAvatarResource(memberAvatar)
        ),
        payAmount = payAmount.toString()
    )
}

fun ReceiptPayerInfo.toMemberInfoUiState(manageMemberResourceProvider: ManageMemberResourceProvider): MemberInfoUiState {
    return MemberInfoUiState(
        memberId = memberId,
        memberName = memberName,
        avatarRes = manageMemberResourceProvider.getAvatarResource(memberAvatar)
    )
}

data class MemberInfoSelectionUiState(
    val memberInfo: MemberInfoUiState = MemberInfoUiState(),
    val isSelected: Boolean = false
)