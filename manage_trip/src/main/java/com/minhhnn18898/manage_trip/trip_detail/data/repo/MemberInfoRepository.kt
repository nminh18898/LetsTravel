package com.minhhnn18898.manage_trip.trip_detail.data.repo

import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfo
import kotlinx.coroutines.flow.Flow

interface MemberInfoRepository {

    companion object {
        const val MAX_MEMBER_ALLOW = 30
    }

    fun getAllMemberInfo(tripId: Long): Flow<List<MemberInfo>>

    suspend fun insertMember(tripId: Long, memberInfo: MemberInfo): Long

    suspend fun updateMemberInfo(tripId: Long, memberInfo: MemberInfo)

    suspend fun deleteMember(memberId: Long)
}