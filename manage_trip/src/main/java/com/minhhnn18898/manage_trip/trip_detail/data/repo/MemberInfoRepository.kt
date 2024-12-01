package com.minhhnn18898.manage_trip.trip_detail.data.repo

import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfo
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
}