package com.minhhnn18898.manage_trip.trip_detail.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel

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