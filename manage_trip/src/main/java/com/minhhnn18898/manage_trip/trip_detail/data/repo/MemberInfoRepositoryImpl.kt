package com.minhhnn18898.manage_trip.trip_detail.data.repo

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.manage_trip.trip_detail.data.dao.MemberInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toMemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toMemberInfoModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MemberInfoRepositoryImpl(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val memberInfoDao: MemberInfoDao
): MemberInfoRepository {

    override fun getAllMemberInfo(tripId: Long): Flow<List<MemberInfo>> =
        memberInfoDao
        .getMembers(tripId)
        .map {
            it.toMemberInfo()
        }

    override suspend fun insertMember(tripId: Long, memberInfo: MemberInfo): Long = withContext(ioDispatcher) {
        val memberInfoModel = memberInfo
            .toMemberInfoModel(tripId)
            .copy(memberId = 0L)

        val resultCode = memberInfoDao.insert(memberInfoModel)
        if(resultCode == -1L) {
            throw ExceptionInsertMemberInfo()
        }

        resultCode
    }

   override suspend fun updateMemberInfo(tripId: Long, memberInfo: MemberInfo) = withContext(ioDispatcher) {
       val memberInfoModel = memberInfo.toMemberInfoModel(tripId)
       val result = memberInfoDao.update(memberInfoModel)
       if(result <= 0) {
           throw ExceptionUpdateMemberInfo()
       }
   }

    override suspend fun deleteMember(memberId: Long) = withContext(ioDispatcher) {
        val result = memberInfoDao.delete(memberId)

        if(result <= 0) {
            throw ExceptionDeleteMemberInfo()
        }
    }
}