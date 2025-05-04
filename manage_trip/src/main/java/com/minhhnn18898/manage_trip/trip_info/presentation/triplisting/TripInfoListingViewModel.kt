package com.minhhnn18898.manage_trip.trip_info.presentation.triplisting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.manage_trip.trip_info.domain.GetListTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.presentation.base.CreateNewTripCtaDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentError
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentLoading
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentResult
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentState
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ICoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripInfoItemDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.toTripItemDisplay
import com.minhhnn18898.trip_data.model.trip_info.TripInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TripInfoListingViewModel @Inject constructor(
    getListTripInfoUseCase: GetListTripInfoUseCase,
    private val defaultCoverResourceProvider: ICoverDefaultResourceProvider
): ViewModel() {

    val contentState: StateFlow<GetSavedTripInfoContentState> =
        getListTripInfoUseCase.execute().map {
            GetSavedTripInfoContentResult(it.makeListTripDisplayItemWithCreateItem())
        }
            .catch<GetSavedTripInfoContentState> {
                emit(GetSavedTripInfoContentError())
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = GetSavedTripInfoContentLoading()
        )

    private fun List<TripInfo>.makeListTripDisplayItemWithCreateItem(): List<TripInfoItemDisplay> {
        if(this.isEmpty()) return emptyList()

        val data = mutableListOf<TripInfoItemDisplay>()
        val userTrips = this.map { tripInfo -> tripInfo.toTripItemDisplay(defaultCoverResourceProvider) }
        data.add(CreateNewTripCtaDisplay)
        data.addAll(userTrips)
        return data
    }
}