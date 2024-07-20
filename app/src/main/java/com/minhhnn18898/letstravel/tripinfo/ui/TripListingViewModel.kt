package com.minhhnn18898.letstravel.tripinfo.ui

import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.usecase.GetListTripInfoUseCase
import kotlinx.coroutines.launch

class TripListingViewModel(
    private val getListTripInfoUseCase: GetListTripInfoUseCase,
    private val defaultCoverResourceProvider: CoverDefaultResourceProvider
): ViewModel() {

    var contentState: ContentState by mutableStateOf(ContentLoading())
        private set

    init {
        loadListTripInfo()
    }

    private fun loadListTripInfo() {
        viewModelScope.launch {
            getListTripInfoUseCase.execute(Unit)?.collect {
                contentState = when(it) {
                    is Result.Loading -> ContentLoading()
                    is Result.Success -> ContentResult(it.data.map { tripInfo -> tripInfo.toTripItemDisplay() })
                    is Result.Error -> ContentError()
                }
            }
        }
    }

    interface ContentState

    class ContentLoading: ContentState

    class ContentResult(val listTripItem: List<TripItemDisplay>): ContentState

    class ContentError: ContentState

    data class TripItemDisplay(val tripName: String, @DrawableRes val defaultCoverRes: Int)

    private fun TripInfo.toTripItemDisplay(): TripItemDisplay {
        return TripItemDisplay(this.title, defaultCoverResourceProvider.getCoverResource(this.defaultCoverId))
    }
}