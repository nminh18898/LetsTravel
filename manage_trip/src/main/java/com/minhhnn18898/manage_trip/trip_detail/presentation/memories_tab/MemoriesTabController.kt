package com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab

import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.manage_trip.trip_detail.domain.photo.GetAllTripPhotosUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MemoriesTabController(
    viewModelScope: CoroutineScope,
    tripId: Long,
    getAllTripPhotosUseCase: GetAllTripPhotosUseCase
) {

    val tripPhotoContentState: StateFlow<UiState<List<PhotoItemUiState>>> =
        getAllTripPhotosUseCase
            .execute(tripId)
            .map { photoInfo ->
                UiState.Success(
                    photoInfo.map {
                        it.toPhotoItemUiState()
                    }
                )
            }
            .catch<UiState<List<PhotoItemUiState>>> {
                emit(UiState.Error())
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileUiSubscribed,
                initialValue = UiState.Loading
            )

}