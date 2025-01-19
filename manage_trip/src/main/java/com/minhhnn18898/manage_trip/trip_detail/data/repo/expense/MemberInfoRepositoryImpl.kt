package com.minhhnn18898.manage_trip.trip_detail.data.repo.expense

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.DefaultBillOwnerDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.MemberInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.DefaultBillOwnerInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.DefaultBillOwnerModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.MemberInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.toBillOwnerInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.toMemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.toMemberInfoModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.SortedSet

class MemberInfoRepositoryImpl(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val memberInfoDao: MemberInfoDao,
    private val defaultBillOwnerDao: DefaultBillOwnerDao
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

        // Default bill owner do not exist, so set this member as default bill owner
        if(defaultBillOwnerDao.getBillOwner(tripId) == null) {
            upsertDefaultBillOwner(tripId, resultCode)
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

    override suspend fun deleteMember(tripId: Long, memberId: Long) = withContext(ioDispatcher) {
        val result = memberInfoDao.delete(memberId)

        if(result <= 0) {
            throw ExceptionDeleteMember()
        }

        // Verify if this member is the default bill owner and remove the corresponding information
        if(defaultBillOwnerDao.getBillOwner(tripId)?.memberId == memberId) {
            defaultBillOwnerDao.delete(tripId)
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

    override suspend fun getDefaultBillOwner(tripId: Long): DefaultBillOwnerInfo? = withContext(ioDispatcher) {
        defaultBillOwnerDao
            .getBillOwner(tripId)
            ?.toBillOwnerInfo()
    }

    override fun getDefaultBillOwnerStream(tripId: Long): Flow<DefaultBillOwnerInfo?> =
        defaultBillOwnerDao
            .getBillOwnerStream(tripId)
            .map {
                it?.toBillOwnerInfo()
            }

    override suspend fun upsertDefaultBillOwner(tripId: Long, memberId: Long): Long = withContext(ioDispatcher) {
        val resultCode = defaultBillOwnerDao
            .insert(
                DefaultBillOwnerModel(
                    tripId = tripId,
                    memberId = memberId
                )
            )

        if(resultCode == -1L) {
            throw ExceptionUpsertDefaultBillOwner()
        }

        resultCode
    }

    override suspend fun deleteDefaultBillOwner(tripId: Long) = withContext(ioDispatcher) {
        val result = defaultBillOwnerDao.delete(tripId)

        if(result <= 0) {
            throw ExceptionDeleteDefaultBillOwner()
        }
    }
}