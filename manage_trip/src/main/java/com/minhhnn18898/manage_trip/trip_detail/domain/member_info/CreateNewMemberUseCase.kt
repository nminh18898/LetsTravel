package com.minhhnn18898.manage_trip.trip_detail.domain.member_info

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.MemberInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.SortedSet
import javax.inject.Inject

class CreateNewMemberUseCase @Inject constructor(private val repository: MemberInfoRepository) {

    fun execute(
        tripId: Long,
        memberInfo: MemberInfo,
        existedMembers: List<MemberInfo>
    ): Flow<Result<Long>> = flow {
        emit(Result.Loading)
        val result = repository.insertMember(
            tripId = tripId,
            memberInfo = memberInfo.copy(
                avatar = findNewAvatarId(existedMembers)
            )
        )
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
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
            .map { it.avatar }
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