package com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.member_info

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.trip_data.repo.expense.MemberInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateNewMemberUseCase @Inject constructor(private val repository: MemberInfoRepository) {

    fun execute(tripId: Long, memberName: String): Flow<Result<Long>> = flow {
        emit(Result.Loading)
        val result = repository.insertMember(
            tripId = tripId,
            memberName = memberName
        )
        emit(Result.Success(result))
    }.catch {
        emit(Result.Error(it))
    }
}