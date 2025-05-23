package com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.main

import androidx.annotation.DrawableRes
import com.minhhnn18898.core.utils.formatWithCommas
import com.minhhnn18898.core.utils.toCompactString
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProvider
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import com.minhhnn18898.trip_data.model.expense.MemberInfo
import com.minhhnn18898.trip_data.model.expense.MemberReceiptPaymentStatisticInfo
import com.minhhnn18898.trip_data.model.expense.ReceiptInfo
import com.minhhnn18898.trip_data.model.expense.ReceiptPayerInfo
import com.minhhnn18898.trip_data.model.expense.ReceiptWithAllPayersInfo

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

interface ReceiptWithAllPayersInfoItemDisplay

data class ReceiptWithAllPayersInfoUiState(
    val receiptInfo: ReceiptInfoUiState,
    val receiptOwner: MemberInfoUiState,
    val receiptPayers: List<ReceiptPayerInfoUiState>
): ReceiptWithAllPayersInfoItemDisplay

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

data class ReceiptWithAllPayersInfoDateSeparatorUiState(
    val description: String
): ReceiptWithAllPayersInfoItemDisplay

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

data class MemberInfoSelectionUiState(
    val memberInfo: MemberInfoUiState = MemberInfoUiState(),
    val isSelected: Boolean = false
)

data class MemberReceiptPaymentStatisticUiState(
    val memberId: Long,
    val memberName: String,
    val paidAmount: Int,
    val ownedAmount: Int,
    val paidAmountDesc: String,
    val ownedAmountDesc: String
)

fun MemberReceiptPaymentStatisticInfo.toMemberReceiptPaymentStatisticUiState(): MemberReceiptPaymentStatisticUiState {
    return MemberReceiptPaymentStatisticUiState(
        memberId = memberId,
        memberName = memberName,
        paidAmount = paidAmount.toInt(),
        ownedAmount = ownedAmount.toInt(),
        paidAmountDesc = paidAmount.toCompactString(),
        ownedAmountDesc = ownedAmount.toCompactString()
    )
}