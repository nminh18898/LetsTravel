package com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab

import com.minhhnn18898.architecture.ui.UiState
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.WhileUiSubscribed
import com.minhhnn18898.manage_trip.trip_detail.data.model.memories.TripMemoriesConfigInfo
import com.minhhnn18898.manage_trip.trip_detail.domain.memories_config.GetMemoriesConfigUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.memories_config.UpdateMemoriesConfigUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.photo.GetAllPhotoFrameTypeUseCase
import com.minhhnn18898.manage_trip.trip_detail.domain.photo.GetAllTripPhotosUseCase
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.BillSplitManageMemberViewModel.ErrorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PhotoFrameSelectionUiState(val options: List<PhotoFrameUiState> = emptyList())

data class MemoriesTabMainUiState(
    val showError: MemoriesTabController.ErrorType = MemoriesTabController.ErrorType.ERROR_MESSAGE_NONE
)

class MemoriesTabController(
    private val viewModelScope: CoroutineScope,
    private val tripId: Long,
    getAllTripPhotosUseCase: GetAllTripPhotosUseCase,
    private val getMemoriesConfigUseCase: GetMemoriesConfigUseCase,
    private val getAllPhotoFrameTypeUseCase: GetAllPhotoFrameTypeUseCase,
    private val resourceProvider: MemoriesTabResourceProvider,
    private val updateMemoriesConfigUseCase: UpdateMemoriesConfigUseCase
) {
    private val _photoFrameSelectionUiState = MutableStateFlow(
        PhotoFrameSelectionUiState(
            options = createDefaultListPhotoFrameOptions()
        )
    )
    val photoFrameSelectionUiState: StateFlow<PhotoFrameSelectionUiState> = _photoFrameSelectionUiState.asStateFlow()

    private val _uiState = MutableStateFlow(MemoriesTabMainUiState())
    val uiState: StateFlow<MemoriesTabMainUiState> = _uiState.asStateFlow()

    val tripPhotoContentState: StateFlow<UiState<List<PhotoItemUiState>>> =
        getAllTripPhotosUseCase
            .execute(tripId)
            .combine(getMemoriesConfigUseCase.execute(tripId)) { photoInfo, photoConfig ->
                UiState.Success(
                    photoInfo.mapIndexed { index, tripPhotoInfo ->
                        val photoFrameResource = resourceProvider.getPhotoFrameResources(photoConfig.photoFrameType, index)
                        tripPhotoInfo.toPhotoItemUiState(
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
                updateNewSelectedPhotoFrame(currentConfig.photoFrameType)
            }
        }
    }

    private fun updateNewSelectedPhotoFrame(currentPhotoFrameType: Int) {
        _photoFrameSelectionUiState.update { state ->
            state.copy(
                options = state.options.map { option ->
                    option.copy(isSelected = option.photoFrameType == currentPhotoFrameType)
                }
            )
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

    fun onChangePhotoFrameType(type: Int) {
        viewModelScope.launch {
            updateMemoriesConfigUseCase.execute(
                tripId = tripId,
                memoriesConfig = TripMemoriesConfigInfo(photoFrameType = type)
            ).collect { result ->
                when(result) {
                    is Result.Error -> {
                        showErrorInBriefPeriod(ErrorType.ERROR_CAN_NOT_CHANGE_FRAME_LAYOUT)
                    }

                    else -> {
                        // no-op
                    }
                }
            }
        }
    }



    @Suppress("SameParameterValue")
    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            _uiState.update { it.copy(showError = errorType) }
            delay(3000)
            _uiState.update { it.copy(showError = ErrorType.ERROR_MESSAGE_NONE) }
        }
    }
    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_CAN_NOT_CHANGE_FRAME_LAYOUT
    }
}