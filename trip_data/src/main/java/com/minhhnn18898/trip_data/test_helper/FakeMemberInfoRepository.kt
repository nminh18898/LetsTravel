package com.minhhnn18898.trip_data.test_helper

import com.minhhnn18898.trip_data.model.expense.DefaultBillOwnerInfo
import com.minhhnn18898.trip_data.model.expense.MemberInfo
import com.minhhnn18898.trip_data.repo.expense.MemberInfoRepository
import kotlinx.coroutines.flow.Flow

class FakeMemberInfoRepository: MemberInfoRepository {
    override fun getAllMemberInfoStream(tripId: Long): Flow<List<MemberInfo>> {
        TODO("Not yet implemented")
    }

    override fun getAllMemberInfo(tripId: Long): List<MemberInfo> {
        TODO("Not yet implemented")
    }

    override suspend fun getMember(memberId: Long): MemberInfo? {
        TODO("Not yet implemented")
    }

    override suspend fun insertMember(tripId: Long, memberName: String): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateMemberInfo(tripId: Long, memberId: Long, memberName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteMember(tripId: Long, memberId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun getDefaultBillOwner(tripId: Long): DefaultBillOwnerInfo? {
        TODO("Not yet implemented")
    }

    override fun getDefaultBillOwnerStream(tripId: Long): Flow<DefaultBillOwnerInfo?> {
        TODO("Not yet implemented")
    }

    override suspend fun upsertDefaultBillOwner(tripId: Long, memberId: Long): Long {
        TODO("Not yet implemented")
    }

    override suspend fun deleteDefaultBillOwner(tripId: Long) {
        TODO("Not yet implemented")
    }
}