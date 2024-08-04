package com.minhhnn18898.letstravel.tripinfo.ui

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.usecase.CreateTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.usecase.GetListDefaultCoverUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTripViewModel @Inject constructor(
    private val getListDefaultCoverUseCase: GetListDefaultCoverUseCase,
    private val createTripInfoUseCase: CreateTripInfoUseCase,
    private val defaultCoverResourceProvider: CoverDefaultResourceProvider
): ViewModel() {

    var tripTitle by mutableStateOf("")
        private set

    var listCoverItems by mutableStateOf(emptyList<CoverUIElement>())
        private set

    var allowSaveContent by mutableStateOf(false)
        private set

    var onShowSaveLoadingState by mutableStateOf(false)
        private set

    var errorType by mutableStateOf(ErrorType.ERROR_MESSAGE_NONE)
        private set

    private val _eventChannel = Channel<Event>()
    val eventTriggerer = _eventChannel.receiveAsFlow()

    init {
        initDefaultCoverList()
    }

    fun onTripTitleUpdated(value: String) {
        tripTitle = value
        checkAllowSaveContent()
    }

    fun onCoverSelected(selectedItem: CoverUIElement) {
        listCoverItems = listCoverItems.map {
            when(it) {
                is DefaultCoverUI -> it.copy(isSelected = it == selectedItem)
                is CustomCoverPhoto -> it.copy(isSelected = it == selectedItem)
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
        val list: List<DefaultCoverUI> = getListDefaultCoverUseCase.execute(Unit)?.map { coverElement ->
            val uiRes = defaultCoverResourceProvider.getDefaultCoverList()[coverElement] ?: 0
            DefaultCoverUI(coverElement.type, isSelected = false, uiRes)
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
            is DefaultCoverUI -> CreateTripInfoUseCase.DefaultCoverParam(tripName, selectedItem.coverId)
            is CustomCoverPhoto -> CreateTripInfoUseCase.CustomCoverParam(tripName, selectedItem.uri)
            else -> null
        }

        if(params != null) {
            viewModelScope.launch {
                createTripInfoUseCase.execute(params)?.collect {
                    onShowSaveLoadingState = it == Result.Loading

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
                is DefaultCoverUI -> it.copy(isSelected = false)
                is CustomCoverPhoto -> it.copy(isSelected = false)
                else -> it
            }
        }
            .toMutableList()
            .apply {
                add(0, CustomCoverPhoto(uri, isSelected = true))
            }
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
        ERROR_MESSAGE_CAN_NOT_CREATE_TRIP_INFO
    }

    abstract class CoverUIElement(open val isSelected: Boolean = false)

    data class DefaultCoverUI(val coverId: Int, override val isSelected: Boolean, @DrawableRes val resId: Int): CoverUIElement(isSelected)

    data class CustomCoverPhoto(val uri: Uri, override val isSelected: Boolean): CoverUIElement(isSelected)

    sealed class Event {
        data object CloseScreen: Event()
    }
}