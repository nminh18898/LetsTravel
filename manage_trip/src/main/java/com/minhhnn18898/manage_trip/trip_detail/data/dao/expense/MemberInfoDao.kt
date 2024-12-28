package com.minhhnn18898.manage_trip.trip_detail.data.dao.expense

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.MemberInfoModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(memberInfoModel: MemberInfoModel): Long

    @Update
    suspend fun update(memberInfoModel: MemberInfoModel): Int

    @Query("DELETE FROM member_info WHERE member_id=:memberId")
    suspend fun delete(memberId: Long): Int

    @Query("SELECT * FROM member_info WHERE trip_id=:tripId")
    fun getMembersStream(tripId: Long): Flow<List<MemberInfoModel>>

    @Query("SELECT * FROM member_info WHERE trip_id=:tripId")
    fun getMembers(tripId: Long): List<MemberInfoModel>

    @Query("SELECT * FROM member_info WHERE member_id=:memberId")
    fun getMember(memberId: Long): MemberInfoModel?
}