package com.minhhnn18898.letstravel.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.domain.GetListTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.presentation.base.CreateNewTripCtaDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentLoading
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentResult
import com.minhhnn18898.manage_trip.trip_info.presentation.base.GetSavedTripInfoContentState
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ICoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripInfoItemDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.toTripItemDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    getListTripInfoUseCase: GetListTripInfoUseCase,
    private val defaultCoverResourceProvider: ICoverDefaultResourceProvider
): ViewModel() {

    val contentState: StateFlow<GetSavedTripInfoContentState> =
        getListTripInfoUseCase.execute().map {
            GetSavedTripInfoContentResult(it.makeListTripDisplayItemWithCreateItem())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GetSavedTripInfoContentLoading()
        )

    private fun List<TripInfo>.makeListTripDisplayItemWithCreateItem(): List<TripInfoItemDisplay> {
        if(this.isEmpty()) return emptyList()

        val data = mutableListOf<TripInfoItemDisplay>()
        val userTrips = this.map { tripInfo -> tripInfo.toTripItemDisplay(defaultCoverResourceProvider) }
        data.addAll(userTrips.take(2))
        data.add(CreateNewTripCtaDisplay)
        return data
    }
}