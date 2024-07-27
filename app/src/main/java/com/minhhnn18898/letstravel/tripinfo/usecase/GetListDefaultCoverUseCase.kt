package com.minhhnn18898.letstravel.tripinfo.usecase

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.letstravel.tripinfo.data.repo.DefaultCoverElement
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import javax.inject.Inject

class GetListDefaultCoverUseCase @Inject constructor(private val repository: TripInfoRepository): UseCase<Unit, List<DefaultCoverElement>>() {

    override fun run(params: Unit): List<DefaultCoverElement> {
        return repository.getListDefaultCoverElements()
    }
}