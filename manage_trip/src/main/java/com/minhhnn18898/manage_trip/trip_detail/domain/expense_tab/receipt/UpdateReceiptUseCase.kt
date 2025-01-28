package com.minhhnn18898.manage_trip.trip_detail.domain.expense_tab.receipt

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.ReceiptRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateReceiptUseCase @Inject constructor(private val repository: ReceiptRepository) {
    fun execute(
        tripId: Long,
        receiptInfo: ReceiptInfo,
        payerInfo: List<ReceiptPayerInfo>
    ): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        repository.updateReceipt(
            tripId = tripId,
            receiptInfo = receiptInfo,
            payerInfo = payerInfo
        )
        emit(Result.Success(Unit))
    }.catch {
        emit(Result.Error(it))
    }
}