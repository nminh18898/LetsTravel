package com.minhhnn18898.manage_trip.trip_detail.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel

@Entity(
    tableName = "member_info",
    foreignKeys = [
        ForeignKey(
            entity = TripInfoModel::class,
            parentColumns = arrayOf("trip_id"),
            childColumns = arrayOf("trip_id"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MemberInfoModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("id")
    val memberId: Long,
    @ColumnInfo("name")
    val memberName: String,
    @ColumnInfo("avatar")
    val avatar: Int,

    // Relation mapping
    @ColumnInfo("trip_id")
    val tripId: Long,
)

data class MemberInfo(
    val memberId: Long = 0,
    val memberName: String,
    val avatarId: Int
)

fun MemberInfo.toMemberInfoModel(tripId: Long): MemberInfoModel {
    return MemberInfoModel(
        memberId = this.memberId,
        memberName = this.memberName,
        avatar = this.avatarId,
        tripId = tripId
    )
}

fun MemberInfoModel.toMemberInfo(): MemberInfo {
    return MemberInfo(
        memberId = this.memberId,
        memberName = this.memberName,
        avatarId = this.avatar
    )
}

fun List<MemberInfoModel>.toMemberInfo(): List<MemberInfo> {
    return this.map { it.toMemberInfo() }
}