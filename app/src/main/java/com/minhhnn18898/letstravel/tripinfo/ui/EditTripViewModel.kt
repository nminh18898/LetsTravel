package com.minhhnn18898.letstravel.tripinfo.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.letstravel.tripinfo.data.repo.DefaultCoverElement
import com.minhhnn18898.letstravel.tripinfo.usecase.CreateTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.usecase.GetListDefaultCoverUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class EditTripViewModel(
    private val getListDefaultCoverUseCase: GetListDefaultCoverUseCase,
    private val createTripInfoUseCase: CreateTripInfoUseCase
): ViewModel() {

    var tripTitle by mutableStateOf("")
        private set

    var listCoverDefault by mutableStateOf(emptyList<DefaultCoverUI>())
        private set

    var allowSaveContent by mutableStateOf(false)
        private set

    private val defaultCoverList = mapOf(
        DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER to R.drawable.trip_cover_default_1,
        DefaultCoverElement.COVER_DEFAULT_THEME_LONG_TRIP to R.drawable.trip_cover_default_2,
        DefaultCoverElement.COVER_DEFAULT_THEME_AROUND_THE_WORLD to R.drawable.trip_cover_default_3,
        DefaultCoverElement.COVER_DEFAULT_THEME_NIGHT_DRIVE to R.drawable.trip_cover_default_4,
        DefaultCoverElement.COVER_DEFAULT_THEME_SEA to R.drawable.trip_cover_default_5,
        DefaultCoverElement.COVER_DEFAULT_THEME_NATURE to R.drawable.trip_cover_default_6
    )

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

    fun onDefaultCoverSelected(coverId: Int) {
        listCoverDefault = listCoverDefault.map {
            it.copy(isSelected = it.coverId == coverId)
        }
        checkAllowSaveContent()
    }

    private fun checkAllowSaveContent() {
        val isValidTitle = tripTitle.isNotBlank() && tripTitle.isNotEmpty()
        val isValidCover = listCoverDefault.any { it.isSelected }

        allowSaveContent = isValidTitle && isValidCover
    }

    private fun initDefaultCoverList() {
        val list: List<DefaultCoverUI> = getListDefaultCoverUseCase.execute(Unit)?.map { coverElement ->
            val uiRes = defaultCoverList[coverElement] ?: 0
            DefaultCoverUI(coverElement.type, isSelected = false, uiRes)
        } ?: emptyList()

        listCoverDefault = list.toMutableStateList()
    }

    fun onSaveClick() {
        val tripName = tripTitle
        val coverId = listCoverDefault.firstOrNull { it.isSelected }?.coverId ?: 0

        viewModelScope.launch {
            createTripInfoUseCase.execute(CreateTripInfoUseCase.Param(tripName, coverId))?.collect {
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

    sealed class Event {
        data object CloseScreen: Event()
    }
}