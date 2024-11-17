package com.minhhnn18898.manage_trip.trip_detail.presentation.trip

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.minhhnn18898.app_navigation.destination.TripDetailDestination
import com.minhhnn18898.app_navigation.destination.TripDetailDestinationParameters
import com.minhhnn18898.app_navigation.mapper.CustomNavType
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.manage_trip.trip_detail.domain.activity.GetSortedListTripActivityInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.flight.GetListFlightInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.hotel.GetListHotelInfoUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.plan_tab.TripDetailPlanTabController
import com.minhhnn18898.manage_trip.trip_info.domain.GetTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ICoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ITripActivityDateSeparatorResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.UserTripDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.toTripItemDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.reflect.typeOf

data class TripDetailScreenTripInfoUiState(
    val tripDisplay: UserTripDisplay? = null,
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false
)

@HiltViewModel
class TripDetailScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val coverResourceProvider: ICoverDefaultResourceProvider,
    activityDateSeparatorResourceProvider: ITripActivityDateSeparatorResourceProvider,
    getTripInfoUseCase: GetTripInfoUseCase,
    getListFlightInfoUseCase: GetListFlightInfoUseCase,
    getListHotelInfoUseCase: GetListHotelInfoUseCase,
    getSortedListTripActivityInfoUseCase: GetSortedListTripActivityInfoUseCase,
    dateTimeFormatter: TripDetailDateTimeFormatter
): ViewModel() {

    private val parameters = savedStateHandle.toRoute<TripDetailDestination>(
        typeMap = mapOf(typeOf<TripDetailDestinationParameters>() to CustomNavType(TripDetailDestinationParameters::class.java, TripDetailDestinationParameters.serializer()))
    ).parameters

    val tripId = parameters.tripId

    val tripInfoContentState: StateFlow<TripDetailScreenTripInfoUiState> =
        getTripInfoUseCase.execute(GetTripInfoUseCase.Param(tripId)).map {
            if(it != null) {
                TripDetailScreenTripInfoUiState(
                    tripDisplay = it.toTripItemDisplay(defaultCoverResourceProvider = coverResourceProvider),
                    isLoading = false
                )
            } else {
                TripDetailScreenTripInfoUiState(
                    isLoading = false,
                    isNotFound = true
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = WhileUiSubscribed,
            initialValue = TripDetailScreenTripInfoUiState(isLoading = true)
        )


    val planTabUIController = TripDetailPlanTabController(
        viewModelScope = viewModelScope,
        tripId = tripId,
        activityDateSeparatorResourceProvider = activityDateSeparatorResourceProvider,
        dateTimeFormatter = dateTimeFormatter,
        getListFlightInfoUseCase = getListFlightInfoUseCase,
        getListHotelInfoUseCase = getListHotelInfoUseCase,
        getSortedListTripActivityInfoUseCase = getSortedListTripActivityInfoUseCase
    )

}

