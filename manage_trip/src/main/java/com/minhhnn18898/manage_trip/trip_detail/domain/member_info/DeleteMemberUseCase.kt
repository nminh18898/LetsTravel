package com.minhhnn18898.manage_trip.trip_detail.domain.member_info

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.repo.MemberInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteMemberUseCase @Inject constructor(private val repository: MemberInfoRepository) {

    fun execute(memberId: Long): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.deleteMember(memberId)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }
}