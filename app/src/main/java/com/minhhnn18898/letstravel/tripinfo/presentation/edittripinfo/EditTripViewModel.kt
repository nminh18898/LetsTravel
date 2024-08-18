package com.minhhnn18898.letstravel.tripinfo.presentation.edittripinfo

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.domain.CreateTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.domain.GetListDefaultCoverUseCase
import com.minhhnn18898.letstravel.tripinfo.domain.GetTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.presentation.base.CoverDefaultResourceProvider
import com.minhhnn18898.letstravel.tripinfo.presentation.base.TripCustomCoverDisplay
import com.minhhnn18898.letstravel.tripinfo.presentation.base.TripDefaultCoverDisplay
import com.minhhnn18898.letstravel.tripinfo.presentation.base.UserTripDisplay
import com.minhhnn18898.letstravel.tripinfo.presentation.base.toTripItemDisplay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTripViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getListDefaultCoverUseCase: GetListDefaultCoverUseCase,
    private val createTripInfoUseCase: CreateTripInfoUseCase,
    private val defaultCoverResourceProvider: CoverDefaultResourceProvider,
    private val getTripInfoUseCase: GetTripInfoUseCase
): ViewModel() {

    private var tripId: Long = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1

    var tripTitle by mutableStateOf("")
        private set

    var listCoverItems by mutableStateOf(emptyList<CoverUIElement>())
        private set

    var allowSaveContent by mutableStateOf(false)
        private set

    var onShowLoadingState by mutableStateOf(false)
        private set

    var errorType by mutableStateOf(ErrorType.ERROR_MESSAGE_NONE)
        private set

    private val _eventChannel = Channel<Event>()
    val eventTriggerer = _eventChannel.receiveAsFlow()

    init {
        initDefaultCoverList()
        loadTripInfo(tripId = tripId)
    }

    private fun loadTripInfo(tripId: Long) {
        if(tripId <= 0) return

        viewModelScope.launch {
            getTripInfoUseCase.execute(GetTripInfoUseCase.Param(tripId))?.collect {
                onShowLoadingState = it == Result.Loading

                when(it) {
                    is Result.Success -> handleResultLoadTripInfo(it.data)
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_LOAD_TRIP_INFO)
                    is Result.Loading -> {
                        /* do nothing */
                    }
                }
            }
        }
    }

    private suspend fun handleResultLoadTripInfo(flowData: Flow<TripInfo>) {
        flowData.collect { item ->
            val itemDisplay = item.toTripItemDisplay(defaultCoverResourceProvider)
            onTripTitleUpdated(itemDisplay.tripName)
            displayCoverFromTrip(itemDisplay)
            checkAllowSaveContent()
        }
    }

    private fun displayCoverFromTrip(tripDisplay: UserTripDisplay) {
        val coverDisplay = tripDisplay.coverDisplay

        if(coverDisplay is TripDefaultCoverDisplay) {
            listCoverItems
                .firstOrNull { coverItem ->
                    coverItem is DefaultCoverElement && coverItem.resId == coverDisplay.defaultCoverRes
                }
                ?.let { coverItem ->
                    onCoverSelected(coverItem)
                }
        } else if(coverDisplay is TripCustomCoverDisplay) {
            onNewPhotoPicked(coverDisplay.coverPath.toUri())
        }
    }

    fun onTripTitleUpdated(value: String) {
        tripTitle = value
        checkAllowSaveContent()
    }

    fun onCoverSelected(selectedItem: CoverUIElement) {
        listCoverItems = listCoverItems.map {
            when(it) {
                is DefaultCoverElement -> it.copy(isSelected = it == selectedItem)
                is CustomCoverPhotoElement -> it.copy(isSelected = it == selectedItem)
                else -> it
            }
        }

        checkAllowSaveContent()
    }

    private fun checkAllowSaveContent() {
        val isValidTitle = tripTitle.isNotBlank() && tripTitle.isNotEmpty()
        val isValidCover = listCoverItems.any { it.isSelected }

        allowSaveContent = isValidTitle && isValidCover
    }

    private fun initDefaultCoverList() {
        val list: List<DefaultCoverElement> = getListDefaultCoverUseCase.execute(Unit)?.map { coverElement ->
            val uiRes = defaultCoverResourceProvider.getDefaultCoverList()[coverElement] ?: 0
            DefaultCoverElement(coverElement.type, isSelected = false, uiRes)
        } ?: emptyList()

        listCoverItems = list.toMutableStateList()
    }

    fun onSaveClick() {
        val tripName = tripTitle
        val selectedItem = listCoverItems.firstOrNull { it.isSelected }

        if(selectedItem == null) {
            showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO)
            return
        }

        val params = when(selectedItem) {
            is DefaultCoverElement -> CreateTripInfoUseCase.DefaultCoverParam(tripName, selectedItem.coverId)
            is CustomCoverPhotoElement -> CreateTripInfoUseCase.CustomCoverParam(tripName, selectedItem.uri)
            else -> null
        }

        if(params != null) {
            viewModelScope.launch {
                createTripInfoUseCase.execute(params)?.collect {
                    onShowLoadingState = it == Result.Loading

                    when(it) {
                        is Result.Success -> _eventChannel.send(Event.CloseScreen)
                        is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO)
                        else -> {
                            // do nothing
                        }
                    }
                }
            }
        }
    }

    fun onNewPhotoPicked(uri: Uri?) {
        if(uri == null) {
            return
        }

        listCoverItems = listCoverItems.map {
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

        checkAllowSaveContent()
    }

    @Suppress("SameParameterValue")
    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            this@EditTripViewModel.errorType = errorType
            delay(3000)
            this@EditTripViewModel.errorType = ErrorType.ERROR_MESSAGE_NONE
        }
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO,
        ERROR_MESSAGE_CAN_NOT_LOAD_TRIP_INFO
    }

    abstract class CoverUIElement(open val isSelected: Boolean = false)

    data class DefaultCoverElement(val coverId: Int, override val isSelected: Boolean, @DrawableRes val resId: Int): CoverUIElement(isSelected)

    data class CustomCoverPhotoElement(val uri: Uri, override val isSelected: Boolean): CoverUIElement(isSelected)

    sealed class Event {
        data object CloseScreen: Event()
    }
}