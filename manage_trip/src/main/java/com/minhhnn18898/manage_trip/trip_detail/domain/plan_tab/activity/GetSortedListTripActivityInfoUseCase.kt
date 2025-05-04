package com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.activity

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import com.minhhnn18898.trip_data.model.plan.TripActivityInfo
import com.minhhnn18898.trip_data.repo.plan.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetSortedListTripActivityInfoUseCase @Inject constructor(
    private val repository: TripDetailRepository,
    private val dateTimeFormatter: TripDetailDateTimeFormatter
): UseCase<GetSortedListTripActivityInfoUseCase.Param, Flow<Map<Long?, List<TripActivityInfo>>>>() {

    override fun run(params: Param): Flow<Map<Long?, List<TripActivityInfo>>> {
        return repository
            .getAllActivityInfo(params.tripId)
            .map { listActivity ->
                listActivity.groupBy {
                    val timestamp = it.timeFrom
                    if(timestamp != null) dateTimeFormatter.getStartOfTheDay(timestamp) else null
                }
            }
    }

    data class Param(val tripId: Long)
}