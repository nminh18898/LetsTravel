package com.minhhnn18898.manage_trip.trip_detail.domain.receipt

import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptWithAllPayersInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.ReceiptRepository
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllReceiptsUseCase @Inject constructor(
    private val repository: ReceiptRepository,
    private val dateTimeFormatter: TripDetailDateTimeFormatter
) {

    fun execute(tripId: Long): Flow<Map<Long, List<ReceiptWithAllPayersInfo>>> {
        return repository.getReceiptsStream(tripId).map { receipts ->
            receipts
                .groupBy {
                    dateTimeFormatter.getStartOfTheDay(it.receiptInfo.createdTime)
                }
                .toSortedMap(reverseOrder())
        }
    }
}