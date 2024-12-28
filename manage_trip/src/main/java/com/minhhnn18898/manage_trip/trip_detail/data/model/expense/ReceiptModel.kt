package com.minhhnn18898.manage_trip.trip_detail.data.model.expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel

@Entity(
    tableName = "receipt",
    foreignKeys = [
        ForeignKey(
            entity = TripInfoModel::class,
            parentColumns = arrayOf("trip_id"),
            childColumns = arrayOf("trip_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ReceiptModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("receipt_id")
    val receiptId: Long,
    @ColumnInfo("name")
    val name: String,
    @ColumnInfo("description")
    val description: String,
    @ColumnInfo("price")
    val price: Long,
    @ColumnInfo("receipt_owner")
    val receiptOwner: Long,
    @ColumnInfo("created_time")
    val createdTime: Long,
    @ColumnInfo("splitting_mode")
    val splittingMode: Int,

    // Relation mapping
    @ColumnInfo("trip_id")
    val tripId: Long
) {
    companion object {
        const val SPLITTING_MODE_NONE = 0
        const val SPLITTING_MODE_EVEN = 1
        const val SPLITTING_MODE_CUSTOM = 2
    }
}

data class ReceiptInfo(
    val receiptId: Long = 0,
    val name: String = "",
    val description: String = "",
    val price: Long = 0,
    val receiptOwner: Long = 0,
    val createdTime: Long = 0,
    val splittingMode: Int = 0
)

fun ReceiptModel.toReceiptInfo(): ReceiptInfo {
    return ReceiptInfo(
        receiptId = receiptId,
        name = name,
        description = description,
        price = price,
        receiptOwner = receiptOwner,
        createdTime = createdTime,
        splittingMode = splittingMode
    )
}

fun ReceiptInfo.toReceiptModel(tripId: Long): ReceiptModel {
    return ReceiptModel(
        receiptId = receiptId,
        name = name,
        description = description,
        price = price,
        receiptOwner = receiptOwner,
        createdTime = createdTime,
        splittingMode = splittingMode,
        tripId = tripId
    )
}