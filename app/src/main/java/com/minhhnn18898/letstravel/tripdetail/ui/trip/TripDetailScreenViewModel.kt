package com.minhhnn18898.letstravel.tripdetail.ui.trip

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.app.navigation.TripDetailDestination
import com.minhhnn18898.letstravel.tripdetail.usecase.GetTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.ui.CoverDefaultResourceProvider
import com.minhhnn18898.letstravel.tripinfo.ui.UserTripItemDisplay
import com.minhhnn18898.letstravel.tripinfo.ui.toTripItemDisplay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class TripDetailScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val defaultResourceProvider: CoverDefaultResourceProvider,
    private val getTripInfoUseCase: GetTripInfoUseCase
): ViewModel() {

    var tripInfoContentState: UiState<UserTripItemDisplay, UiState.UndefinedError> by mutableStateOf(UiState.Loading)
        private set

    init {
        val tripId = savedStateHandle.get<Long>(TripDetailDestination.tripIdArg) ?: -1
        loadTripInfo(tripId)
    }

    private fun loadTripInfo(tripId: Long) {
        viewModelScope.launch {
            getTripInfoUseCase.execute(GetTripInfoUseCase.Param(tripId))?.collect {
                when(it) {
                    is Result.Loading -> tripInfoContentState = UiState.Loading
                    is Result.Success -> handleResultLoadTripInfo(it.data)
                    is Result.Error -> tripInfoContentState = UiState.Error(UiState.UndefinedError)
                }
            }
        }
    }

    private suspend fun handleResultLoadTripInfo(flowData: Flow<TripInfo>) {
        flowData.collect { item ->
            tripInfoContentState = UiState.Success(item.toTripItemDisplay(defaultResourceProvider))
        }
    }
}