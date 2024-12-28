package com.minhhnn18898.manage_trip.trip_detail.data.model.expense

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = "receipt_payer",
    primaryKeys = ["member_id", "receipt_id"]
)
data class ReceiptPayerModel(
    @ColumnInfo("member_id")
    val memberId: Long,
    @ColumnInfo("receipt_id")
    val receiptId: Long,
    @ColumnInfo("pay_amount")
    val payAmount: Long
)

data class ReceiptPayerInfo(
    val memberId: Long = 0,
    val memberName: String = "",
    val memberAvatar: Int = 0,
    val payAmount: Long = 0
)

fun ReceiptPayerInfo.toReceiptPayerModel(receiptId: Long): ReceiptPayerModel {
    return ReceiptPayerModel(
        memberId = this.memberId,
        receiptId = receiptId,
        payAmount = this.payAmount
    )
}

data class ReceiptWithPayersModel(
    val receiptId: Long,
    val receiptName: String,
    val description: String,
    val price: Long,
    val createdTime: Long,
    val splittingMode: Int,
    val tripId: Long,

    val receiptOwnerId: Long,
    val ownerName: String,
    val ownerAvatar: Int,

    val payerId: Long,
    val payerName: String,
    val payerAvatar: Int,
    val payAmount: Long
)

data class ReceiptWithAllPayersInfo(
    val receiptInfo: ReceiptInfo,
    val receiptOwner: MemberInfo,
    val receiptPayers: List<ReceiptPayerInfo>
)

private fun ReceiptWithPayersModel.getReceiptInfo(): ReceiptInfo {
    return ReceiptInfo(
        receiptId = this.receiptId,
        name = this.receiptName,
        description = this.description,
        price = this.price,
        receiptOwner = this.receiptOwnerId,
        createdTime = this.createdTime,
        splittingMode = this.splittingMode
    )
}

private fun List<ReceiptWithPayersModel>.getReceiptInfo(): ReceiptInfo {
    return this.firstOrNull()?.getReceiptInfo() ?: ReceiptInfo()
}

private fun ReceiptWithPayersModel.getReceiptOwnerInfo(): MemberInfo {
    return MemberInfo(
        memberId = this.receiptOwnerId,
        memberName = this.ownerName,
        avatarId = this.ownerAvatar
    )
}

private fun List<ReceiptWithPayersModel>.getReceiptOwnerInfo(): MemberInfo {
    return this.firstOrNull()?.getReceiptOwnerInfo() ?: MemberInfo()
}

private fun List<ReceiptWithPayersModel>.getReceiptPayersInfo(): List<ReceiptPayerInfo> {
    return this.map { receiptDetail ->
        ReceiptPayerInfo(
            memberId = receiptDetail.payerId,
            memberName = receiptDetail.payerName,
            memberAvatar = receiptDetail.payerAvatar,
            payAmount = receiptDetail.payAmount
        )
    }
}

fun List<ReceiptWithPayersModel>.toReceiptWithAllPayersInfo(): List<ReceiptWithAllPayersInfo> {
    return this
        .groupBy {
            it.receiptId
        }
        .values
        .map {
            ReceiptWithAllPayersInfo(
                receiptInfo =  it.getReceiptInfo(),
                receiptOwner = it.getReceiptOwnerInfo(),
                receiptPayers = it.getReceiptPayersInfo()
            )
        }
}
