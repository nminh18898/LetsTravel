package com.minhhnn18898.manage_trip.trip_detail.domain.member_info

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.MemberInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateMemberInfoUseCase @Inject constructor(private val repository: MemberInfoRepository) {

    fun execute(tripId: Long, memberInfo: MemberInfo): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.updateMemberInfo(tripId, memberInfo)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

}