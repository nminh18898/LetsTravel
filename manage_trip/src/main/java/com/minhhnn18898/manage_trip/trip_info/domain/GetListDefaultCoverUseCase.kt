package com.minhhnn18898.manage_trip.trip_info.domain

import com.minhhnn18898.manage_trip.trip_info.data.repo.DefaultCoverElement
import com.minhhnn18898.manage_trip.trip_info.data.repo.TripInfoRepository
import javax.inject.Inject

class GetListDefaultCoverUseCase @Inject constructor(private val repository: TripInfoRepository) {

    fun execute(): List<DefaultCoverElement> {
        return repository.getListDefaultCoverElements()
    }
}