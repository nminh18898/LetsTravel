package com.minhhnn18898.manage_trip.trip_detail.data.repo

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.manage_trip.trip_detail.data.dao.MemberInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.toMemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toMemberInfoModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.SortedSet

class MemberInfoRepositoryImpl(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val memberInfoDao: MemberInfoDao
): MemberInfoRepository {

    override fun getAllMemberInfoStream(tripId: Long): Flow<List<MemberInfo>> =
        memberInfoDao
        .getMembersStream(tripId)
        .map {
            it.toMemberInfo()
        }

    override fun getAllMemberInfo(tripId: Long): List<MemberInfo> =
        memberInfoDao
            .getMembers(tripId)
            .map {
                it.toMemberInfo()
            }

    override suspend fun getMember(memberId: Long): MemberInfo? = withContext(ioDispatcher) {
            memberInfoDao
                .getMember(memberId)
                ?.toMemberInfo()
        }

    override suspend fun insertMember(tripId: Long, memberName: String): Long = withContext(ioDispatcher) {
        val currentMembers = getAllMemberInfo(tripId)

        if(currentMembers.size >= MemberInfoRepository.MAX_MEMBER_ALLOW) {
            throw ExceptionAddMember()
        }

        val memberInfoModel = MemberInfoModel(
            memberId = 0,
            memberName = memberName,
            avatar = findNewAvatarId(currentMembers),
            tripId = tripId
        )

        val resultCode = memberInfoDao.insert(memberInfoModel)
        if(resultCode == -1L) {
            throw ExceptionAddMember()
        }

        resultCode
    }

   override suspend fun updateMemberInfo(
       tripId: Long,
       memberId: Long,
       memberName: String
   ) = withContext(ioDispatcher) {
       val memberInfo = getMember(memberId) ?: throw ExceptionUpdateMemberInfo()

       val memberInfoModel = memberInfo
           .toMemberInfoModel(tripId)
           .copy(
               memberName = memberName
           )

       val result = memberInfoDao.update(memberInfoModel)

       if(result <= 0) {
           throw ExceptionUpdateMemberInfo()
       }
   }

    override suspend fun deleteMember(memberId: Long) = withContext(ioDispatcher) {
        val result = memberInfoDao.delete(memberId)

        if(result <= 0) {
            throw ExceptionDeleteMember()
        }
    }

    /**
     * Finds the first available avatar ID for a new member.

     * 1. Extracts the avatar IDs from the existing members.
     * 2. Sorts the avatar IDs into a sorted set to ensure uniqueness.
     * 3. Finds the first non-negative integer that is not present in the sorted set of avatar IDs.
     * 4. Returns the found index as the available avatar ID.

     * If all avatar IDs up to the maximum limit are taken, 0 is returned.

     * @param existedMembers A list of existing members.
     * @return The first available avatar ID, or 0 if none are available.
     */
    private fun findNewAvatarId(existedMembers: List<MemberInfo>): Int {
        return existedMembers
            .map { it.avatarId }
            .toSortedSet()
            .findFirstAvailableIndex()
    }

    /**
     * Finds the first available index within the maximum allowed member limit.
     *
     * Iterates through integers from 0 to [MemberInfoRepository.MAX_MEMBER_ALLOW] - 1.
     * Returns the first integer that is not present in the sorted set.
     * If all indices up to the maximum limit are taken, 0 is returned.
     *
     * @return The first available index within the maximum member limit, or 0 if none are available.
     */
    private fun SortedSet<Int>.findFirstAvailableIndex(): Int {
        for(i in 0 until MemberInfoRepository.MAX_MEMBER_ALLOW) {
            if(!this.contains(i)) {
                return i
            }
        }

        return 0
    }
}