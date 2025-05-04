package com.minhhnn18898.manage_trip.trip_info.domain

import com.minhhnn18898.trip_data.repo.trip_info.DefaultCoverElement
import com.minhhnn18898.trip_data.repo.trip_info.TripInfoRepository
import javax.inject.Inject

class GetListDefaultCoverUseCase @Inject constructor(private val repository: TripInfoRepository) {

    fun execute(): List<DefaultCoverElement> {
        return repository.getListDefaultCoverElements()
    }
}