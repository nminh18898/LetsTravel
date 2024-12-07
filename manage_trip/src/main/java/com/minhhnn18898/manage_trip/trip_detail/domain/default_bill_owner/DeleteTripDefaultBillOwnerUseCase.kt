package com.minhhnn18898.manage_trip.trip_detail.domain.default_bill_owner

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.repo.MemberInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTripDefaultBillOwnerUseCase @Inject constructor(private val repository: MemberInfoRepository) {

    fun execute(tripId: Long): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.deleteDefaultBillOwner(tripId)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }

}