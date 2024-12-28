package com.minhhnn18898.manage_trip.trip_detail.data.repo.expense

import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.DefaultBillOwnerInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.MemberInfo
import kotlinx.coroutines.flow.Flow

interface MemberInfoRepository {

    companion object {
        const val MAX_MEMBER_ALLOW = 30
    }

    fun getAllMemberInfoStream(tripId: Long): Flow<List<MemberInfo>>

    fun getAllMemberInfo(tripId: Long): List<MemberInfo>

    suspend fun getMember(memberId: Long): MemberInfo?

    suspend fun insertMember(tripId: Long, memberName: String): Long

    suspend fun updateMemberInfo(tripId: Long, memberId: Long, memberName: String)

    suspend fun deleteMember(memberId: Long)

    suspend fun getDefaultBillOwner(tripId: Long): DefaultBillOwnerInfo?

    fun getDefaultBillOwnerStream(tripId: Long): Flow<DefaultBillOwnerInfo?>

    suspend fun upsertDefaultBillOwner(tripId: Long, memberId: Long): Long

    suspend fun deleteDefaultBillOwner(tripId: Long)
}