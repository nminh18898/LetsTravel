package com.minhhnn18898.letstravel.tripinfo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.usecase.GetListTripInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TripInfoListingViewModel(
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
            data.add(CreateNewTripItemDisplay)
            data.addAll(userTrips)
            contentState = GetSavedTripInfoContentResult(data)
        }
    }

}