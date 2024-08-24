package com.minhhnn18898.letstravel.tripdetail.presentation.activity

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.letstravel.tripdetail.domain.activity.CreateTripActivityInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.activity.DeleteTripActivityInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.activity.GetTripActivityInfoUseCase
import com.minhhnn18898.letstravel.tripdetail.domain.activity.UpdateTripActivityInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditTripActivityViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val createTripActivityInfoUseCase: CreateTripActivityInfoUseCase,
    private val getTripActivityInfoUseCase: GetTripActivityInfoUseCase,
    private val updateTripActivityInfoUseCase: UpdateTripActivityInfoUseCase,
    private val deleteTripActivityInfoUseCase: DeleteTripActivityInfoUseCase
): ViewModel() {

    private var tripId: Long = savedStateHandle.get<Long>(MainAppRoute.tripIdArg) ?: -1
    private var activityId: Long = savedStateHandle.get<Long>(MainAppRoute.activityIdArg) ?: 0L

    var uiState = mutableStateOf(EditTripActivityUiState())
        private set

    fun onPhotoUpdated(value: String) {
        uiState.value = uiState.value.copy(photo = value)
    }

    fun onNameUpdated(value: String) {
        uiState.value = uiState.value.copy(name = value)
    }

    fun onDescriptionUpdated(value: String) {
        uiState.value = uiState.value.copy(description = value)
    }

    fun onPricesUpdated(value: String) {
        uiState.value = uiState.value.copy(prices = value)
    }

    fun onDateUpdated(date: Long?) {
        uiState.value = uiState.value.copy(date = date)
    }

    fun onTimeFromUpdated(value: Pair<Int, Int>) {
        uiState.value = uiState.value.copy(timeFrom = value)
    }

    fun onTimeToUpdated(value: Pair<Int, Int>) {
        uiState.value = uiState.value.copy(timeTo = value)
    }

    data class EditTripActivityUiState(
        val photo: String = "",
        val name: String = "",
        val description: String = "",
        val prices: String = "",
        val date: Long? = null,
        val timeFrom: Pair<Int, Int> = Pair(0, 0),
        val timeTo: Pair<Int, Int> = Pair(0, 0)
    )
}