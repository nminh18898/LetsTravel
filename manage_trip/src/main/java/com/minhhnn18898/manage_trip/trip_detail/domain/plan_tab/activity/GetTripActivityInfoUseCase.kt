package com.minhhnn18898.manage_trip.trip_detail.domain.plan_tab.activity

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.trip_data.model.plan.TripActivityInfo
import com.minhhnn18898.trip_data.repo.plan.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTripActivityInfoUseCase @Inject constructor(private val repository: TripDetailRepository): UseCase<GetTripActivityInfoUseCase.Param, Flow<TripActivityInfo?>>() {

    override fun run(params: Param): Flow<TripActivityInfo?> = repository.getActivityInfo(params.activityId)

    data class Param(val activityId: Long)
}