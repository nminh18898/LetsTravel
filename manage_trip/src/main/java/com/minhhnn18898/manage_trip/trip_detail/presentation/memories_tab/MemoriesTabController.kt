package com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab

import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.manage_trip.trip_detail.domain.memories_config.GetMemoriesConfigUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.photo.GetAllPhotoFrameTypeUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.photo.GetAllTripPhotosUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhotoFrameSelectionUiState(val options: List<PhotoFrameUiState> = emptyList())

class MemoriesTabController(
    private val viewModelScope: CoroutineScope,
    private val tripId: Long,
    getAllTripPhotosUseCase: GetAllTripPhotosUseCase,
    private val getMemoriesConfigUseCase: GetMemoriesConfigUseCase,
    private val getAllPhotoFrameTypeUseCase: GetAllPhotoFrameTypeUseCase,
    private val resourceProvider: MemoriesTabResourceProvider
) {
    private val _photoFrameSelectionUiState = MutableStateFlow(
        PhotoFrameSelectionUiState(
            options = createDefaultListPhotoFrameOptions()
        )
    )
    val photoFrameSelectionUiState: StateFlow<PhotoFrameSelectionUiState> = _photoFrameSelectionUiState.asStateFlow()

    val tripPhotoContentState: StateFlow<UiState<List<PhotoItemUiState>>> =
        getAllTripPhotosUseCase
            .execute(tripId)
            .combine(getMemoriesConfigUseCase.execute(tripId)) { photoInfo, photoConfig ->
                val photoFrameResource = resourceProvider.getPhotoFrameResources(photoConfig.photoFrameType)
                UiState.Success(
                    photoInfo.map {
                        it.toPhotoItemUiState(
                            backgroundRes = photoFrameResource.first,
                            decorationRes = photoFrameResource.second
                        )
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

    init {
        loadMemoriesConfig()
    }

    private fun loadMemoriesConfig() {
        viewModelScope.launch {
            getMemoriesConfigUseCase.execute(tripId).collect { currentConfig ->
                _photoFrameSelectionUiState.update { state ->
                    state.copy(
                        options = state.options.map { option ->
                            option.copy(isSelected = option.photoFrameType == currentConfig.photoFrameType)
                        }
                    )
                }
            }
        }
    }

    private fun createDefaultListPhotoFrameOptions(): List<PhotoFrameUiState> {
        return getAllPhotoFrameTypeUseCase
            .execute()
            .map {
                val photoFrameResource = resourceProvider.getPhotoFrameResources(it)

                PhotoFrameUiState(
                    photoFrameType = it,
                    photoFrameNameRes = resourceProvider.getPhotoFrameNameRes(it),
                    backgroundRes = photoFrameResource.first,
                    decorationRes = photoFrameResource.second
                )
            }
    }

}