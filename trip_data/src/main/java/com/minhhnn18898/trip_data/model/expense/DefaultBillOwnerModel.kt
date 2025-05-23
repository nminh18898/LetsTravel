package com.minhhnn18898.trip_data.model.expense

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minhhnn18898.trip_data.model.trip_info.TripInfoModel

@Entity(
    tableName = "default_bill_owner_model",
    foreignKeys = [
        ForeignKey(
            entity = TripInfoModel::class,
            parentColumns = arrayOf("trip_id"),
            childColumns = arrayOf("trip_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DefaultBillOwnerModel(
    @PrimaryKey
    @ColumnInfo("trip_id")
    val tripId: Long,

    @ColumnInfo("member_id")
    val memberId: Long
)

data class DefaultBillOwnerInfo(
    val tripId: Long,
    val memberId: Long
)

fun DefaultBillOwnerModel.toBillOwnerInfo(): DefaultBillOwnerInfo {
    return DefaultBillOwnerInfo(
        tripId = tripId,
        memberId = memberId
    )
}