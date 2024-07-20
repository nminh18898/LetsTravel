package com.minhhnn18898.letstravel.homescreen

import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.ui.CoverDefaultResourceProvider
import com.minhhnn18898.letstravel.tripinfo.usecase.GetListTripInfoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HomeScreenViewModel(
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
                when(it) {
                    is Result.Loading -> contentState = ContentLoading()
                    is Result.Success -> handleResultLoadListTripInfo(it.data)
                    is Result.Error -> contentState = ContentError()
                }
            }
        }
    }

    private suspend fun handleResultLoadListTripInfo(flowData: Flow<List<TripInfo>>) {
        flowData.collect { item ->
            val data = mutableListOf<TripItemDisplay>()
            val userTrips = item.map { tripInfo -> tripInfo.toTripItemDisplay() }
            data.addAll(userTrips.take(2))
            data.add(CreateNewTripItem)
            contentState = ContentResult(data)
        }
    }

    interface ContentState

    class ContentLoading: ContentState

    class ContentResult(val listTripItem: List<TripItemDisplay>): ContentState

    class ContentError: ContentState

    interface TripItemDisplay

    data class UserTripItem(val tripName: String, @DrawableRes val defaultCoverRes: Int): TripItemDisplay

    data object CreateNewTripItem: TripItemDisplay

    private fun TripInfo.toTripItemDisplay(): UserTripItem {
        return UserTripItem(this.title, defaultCoverResourceProvider.getCoverResource(this.defaultCoverId))
    }
}