package com.minhhnn18898.manage_trip.trip_detail.domain.receipt

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteReceiptUseCase @Inject constructor(private val repository: ReceiptRepository) {

    fun execute(receiptId: Long): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.deleteReceipt(receiptId)
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }
}