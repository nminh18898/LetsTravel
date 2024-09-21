package com.minhhnn18898.manage_trip.trip_info.presentation.edittripinfo

import androidx.annotation.DrawableRes
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfo
import com.minhhnn18898.manage_trip.trip_info.domain.CreateTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.domain.DeleteTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.domain.GetListDefaultCoverUseCase
import com.minhhnn18898.manage_trip.trip_info.domain.GetTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.domain.ModifyTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.domain.UpdateTripInfoUseCase
import com.minhhnn18898.manage_trip.trip_info.presentation.base.CoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripCustomCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripDefaultCoverDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.base.toTripItemDisplay
import com.minhhnn18898.manage_trip.trip_info.presentation.edittripinfo.AddEditTripViewModel.CoverUIElement
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddEditTripInfoUiState(
    val tripTitle: String = "",
    val listCoverItems: List<CoverUIElement> = emptyList(),
    val isLoading: Boolean = false,
    val isNotFound: Boolean = false,
    val canDeleteTrip: Boolean = false,
    val isShowDeleteConfirmation: Boolean = false,
    val showError: AddEditTripViewModel.ErrorType = AddEditTripViewModel.ErrorType.ERROR_MESSAGE_NONE,
    val newCreatedTripUiState: NewCreatedTripUiState? = null,
    val isTripUpdated: Boolean = false,
    val isTripDeleted: Boolean = false,
    val allowSaveContent: Boolean = false
)

data class NewCreatedTripUiState(val tripId: Long)

@HiltViewModel
class AddEditTripViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getListDefaultCoverUseCase: GetListDefaultCoverUseCase,
    private val createTripInfoUseCase: CreateTripInfoUseCase,
    private val defaultCoverResourceProvider: CoverDefaultResourceProvider,
    private val getTripInfoUseCase: GetTripInfoUseCase,
    private val updateTripInfoUseCase: UpdateTripInfoUseCase,
    private val deleteTripInfoUseCase: DeleteTripInfoUseCase
): ViewModel() {

    private var tripId: Long = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1

    private val _uiState = MutableStateFlow(AddEditTripInfoUiState())
    val uiState: StateFlow<AddEditTripInfoUiState> = _uiState.asStateFlow()

    init {
        if(tripId > 0) {
            loadTripInfo()
        } else {
            _uiState.update {
                it.copy(
                    listCoverItems = getListCoverDefault()
                )
            }
        }
    }

    private fun loadTripInfo() {
        _uiState.update {
            it.copy(isLoading = true)
        }

        viewModelScope.launch {
            getTripInfoUseCase.execute(GetTripInfoUseCase.Param(tripId)).collect { tripInfo ->
                if(tripInfo != null) {
                    _uiState.update {
                        it.copy(
                            tripTitle = tripInfo.title,
                            listCoverItems = getListCoverFromTrip(tripInfo),
                            isLoading = false,
                            canDeleteTrip = true
                        )
                    }
                    checkAllowSaveContent()
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isNotFound = true
                        )
                    }
                }
            }
        }
    }

    private fun getListCoverDefault(): List<CoverUIElement> {
        return getListDefaultCoverUseCase.execute()
            .map { coverElement ->
                val uiRes = defaultCoverResourceProvider.getDefaultCoverList()[coverElement] ?: 0
                DefaultCoverElement(coverElement.type, isSelected = false, uiRes)
            }
    }

    private fun getListCoverFromTrip(tripInfo: TripInfo): List<CoverUIElement> {
        val listCoverItems = uiState.value.listCoverItems.ifEmpty { getListCoverDefault() }

        val tripDisplay = tripInfo.toTripItemDisplay(defaultCoverResourceProvider)
        val coverDisplay = tripDisplay.coverDisplay

        if(coverDisplay is TripDefaultCoverDisplay) {
            listCoverItems
                .firstOrNull { coverItem ->
                    coverItem is DefaultCoverElement && coverItem.resId == coverDisplay.defaultCoverRes
                }
                ?.let { coverItem ->
                    return getListCoverWithSelectedItem(coverItem)
                }
        } else if(coverDisplay is TripCustomCoverDisplay) {
            return getListCoverWithCustomPhotoPicked(coverDisplay.coverPath)
        }

        return listCoverItems
    }

    fun onTripTitleUpdated(value: String) {
        _uiState.update {
            it.copy(
                tripTitle = value
            )
        }

        checkAllowSaveContent()
    }

    private fun getListCoverWithSelectedItem(selectedItem: CoverUIElement): List<CoverUIElement> {
        val listCoverItems = uiState.value.listCoverItems.ifEmpty { getListCoverDefault() }

        return listCoverItems.map {
            when(it) {
                is DefaultCoverElement -> it.copy(isSelected = it == selectedItem)
                is CustomCoverPhotoElement -> it.copy(isSelected = it == selectedItem)
                else -> it
            }
        }
    }

    fun onCoverSelected(selectedItem: CoverUIElement) {
        _uiState.update { editUiState ->
            editUiState.copy(listCoverItems = uiState.value.listCoverItems.map {
                when (it) {
                    is DefaultCoverElement -> it.copy(isSelected = it == selectedItem)
                    is CustomCoverPhotoElement -> it.copy(isSelected = it == selectedItem)
                    else -> it
                }
            })
        }

        checkAllowSaveContent()
    }

    fun onSaveClick() {
        val tripName = uiState.value.tripTitle
        val selectedItem = uiState.value.listCoverItems.firstOrNull { it.isSelected }

        if(selectedItem == null) {
            _uiState.update {
                it.copy(
                    showError = ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO
                )
            }
            return
        }

        val params = when(selectedItem) {
            is DefaultCoverElement -> ModifyTripInfoUseCase.DefaultCoverParam(tripId, tripName, selectedItem.coverId)
            is CustomCoverPhotoElement -> ModifyTripInfoUseCase.CustomCoverParam(tripId, tripName, selectedItem.uri)
            else -> null
        }

        if(params != null) {
            if(isUpdateExistingInfo()) {
                updateTrip(params)
            } else {
                createNewTrip(params)
            }
        }
    }

    private fun createNewTrip(param: ModifyTripInfoUseCase.Param) {
        viewModelScope.launch {
            createTripInfoUseCase.execute(param).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                newCreatedTripUiState = NewCreatedTripUiState(tripId = result.data)
                            )
                        }
                    }
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO)
                }
            }
        }
    }

    private fun updateTrip(param: ModifyTripInfoUseCase.Param) {
        viewModelScope.launch {
            updateTripInfoUseCase.execute(param).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isTripUpdated = true
                            )
                        }
                    }
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_UPDATE_TRIP_INFO)
                }
            }
        }
    }

    private fun getListCoverWithCustomPhotoPicked(uri: String): List<CoverUIElement> {
        val listCoverItems = uiState.value.listCoverItems.ifEmpty { getListCoverDefault() }

        return listCoverItems.map {
            when(it) {
                is DefaultCoverElement -> it.copy(isSelected = false)
                is CustomCoverPhotoElement -> it.copy(isSelected = false)
                else -> it
            }
        }
            .toMutableList()
            .apply {
                add(0, CustomCoverPhotoElement(uri, isSelected = true))
            }
    }

    fun onNewPhotoPicked(uri: String) {
        _uiState.update {
            it.copy(
                listCoverItems = getListCoverWithCustomPhotoPicked(uri)
            )
        }

        checkAllowSaveContent()
    }

    private fun isUpdateExistingInfo(): Boolean {
        return tripId > 0L
    }

    fun onDeleteClick() {
        _uiState.update {
            it.copy(isShowDeleteConfirmation = true)
        }
    }

    fun onDeleteConfirm() {
        _uiState.update {
            it.copy(isShowDeleteConfirmation = false)
        }

        viewModelScope.launch {
            deleteTripInfoUseCase.execute(DeleteTripInfoUseCase.Param(tripId)).collect { result ->
                when(result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isTripDeleted = true
                            )
                        }
                    }
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_DELETE_TRIP_INFO)
                }
            }
        }
    }

    fun onDeleteDismiss() {
        _uiState.update {
            it.copy(isShowDeleteConfirmation = false)
        }
    }

    private fun checkAllowSaveContent() {
        _uiState.update {
            it.copy(allowSaveContent = isAllowSave())
        }
    }

    private fun isAllowSave(): Boolean {
        if(uiState.value.isLoading || uiState.value.isNotFound)
            return false

        val tripTitle = uiState.value.tripTitle
        val listCoverItems = uiState.value.listCoverItems
        val isValidTitle = tripTitle.isNotBlank() && tripTitle.isNotEmpty()
        val isValidCover = listCoverItems.any { it.isSelected }

        return isValidTitle && isValidCover
    }

    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    showError = errorType
                )
            }
            delay(3000)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    showError = ErrorType.ERROR_MESSAGE_NONE
                )
            }
        }
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO,
        ERROR_MESSAGE_CAN_NOT_LOAD_TRIP_INFO,
        ERROR_MESSAGE_CAN_NOT_UPDATE_TRIP_INFO,
        ERROR_MESSAGE_CAN_NOT_DELETE_TRIP_INFO
    }

    abstract class CoverUIElement(open val isSelected: Boolean = false)

    data class DefaultCoverElement(val coverId: Int, override val isSelected: Boolean, @DrawableRes val resId: Int): CoverUIElement(isSelected)

    data class CustomCoverPhotoElement(val uri: String, override val isSelected: Boolean): CoverUIElement(isSelected)
}