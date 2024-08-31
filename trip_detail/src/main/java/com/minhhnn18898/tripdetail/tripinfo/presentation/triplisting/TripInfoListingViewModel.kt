package com.minhhnn18898.tripdetail.tripinfo.presentation.triplisting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.tripdetail.tripinfo.data.model.TripInfo
import com.minhhnn18898.tripdetail.tripinfo.domain.GetListTripInfoUseCase
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.CoverDefaultResourceProvider
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.CreateNewTripCtaDisplay
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.GetSavedTripInfoContentError
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.GetSavedTripInfoContentLoading
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.GetSavedTripInfoContentResult
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.GetSavedTripInfoContentState
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.TripInfoItemDisplay
import com.minhhnn18898.tripdetail.tripinfo.presentation.base.toTripItemDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TripInfoListingViewModel @Inject constructor(
    private val getListTripInfoUseCase: GetListTripInfoUseCase,
    private val defaultCoverResourceProvider: CoverDefaultResourceProvider
): ViewModel() {

    var contentState: GetSavedTripInfoContentState by mutableStateOf(GetSavedTripInfoContentLoading())
        private set

    init {
        loadListTripInfo()
    }

    private fun loadListTripInfo() {
        viewModelScope.launch {
            getListTripInfoUseCase.execute(Unit)?.collect {
                when(it) {
                    is Result.Loading -> contentState = GetSavedTripInfoContentLoading()
                    is Result.Success -> handleResultLoadListTripInfo(it.data)
                    is Result.Error -> contentState = GetSavedTripInfoContentError()
                }
            }
        }
    }

    private suspend fun handleResultLoadListTripInfo(flowData: Flow<List<TripInfo>>) {
        flowData.collect { item ->
            val data = mutableListOf<TripInfoItemDisplay>()
            val userTrips = item.map { tripInfo -> tripInfo.toTripItemDisplay(defaultCoverResourceProvider) }
            data.add(CreateNewTripCtaDisplay)
            data.addAll(userTrips)
            contentState = GetSavedTripInfoContentResult(data)
        }
    }

}