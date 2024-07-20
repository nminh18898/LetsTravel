package com.minhhnn18898.letstravel.tripinfo.ui

import androidx.lifecycle.ViewModel
import com.minhhnn18898.letstravel.tripinfo.usecase.GetListTripInfoUseCase

class TripInfoListingViewModel(
    private val getListTripInfoUseCase: GetListTripInfoUseCase,
    private val defaultCoverResourceProvider: CoverDefaultResourceProvider
): ViewModel() {

}