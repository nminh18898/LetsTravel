package com.minhhnn18898.manage_trip.tripinfo.data

import com.minhhnn18898.manage_trip.tripinfo.data.model.ExceptionDeleteTripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.ExceptionInsertTripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.ExceptionUpdateTripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.tripinfo.data.model.toTripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.repo.DefaultCoverElement
import com.minhhnn18898.manage_trip.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.jetbrains.annotations.TestOnly

class FakeTripInfoRepository: TripInfoRepository {

    private var autoGeneratedId = 1L

    private val tripInfoMap: LinkedHashMap<Long, TripInfo> = LinkedHashMap()

    private val tripInfosFlow: MutableSharedFlow<List<TripInfo>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    var forceError = false

    override fun getAllTrips(): Flow<List<TripInfo>> {
        if(forceError) {
            throw Exception("Internal Error. Can not get trip info")
        }

        if(tripInfoMap.isNotEmpty()) {
            return tripInfosFlow
        }

        return flow { emit(emptyList()) }
    }

    override fun getTrip(id: Long): Flow<TripInfo?> {
        if(forceError) {
            throw Exception("Internal Error. Can not get trip info")
        }

        if(tripInfoMap.containsKey(id)) {
            return tripInfosFlow.map { tripInfoMap[id] }
        }

       return flow { emit(null) }
    }

    override fun getListDefaultCoverElements(): List<DefaultCoverElement> = listOf(
            DefaultCoverElement.COVER_DEFAULT_THEME_SPRING,
            DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER,
            DefaultCoverElement.COVER_DEFAULT_THEME_AUTUMN,
            DefaultCoverElement.COVER_DEFAULT_THEME_WINTER,
            DefaultCoverElement.COVER_DEFAULT_THEME_BEACH,
            DefaultCoverElement.COVER_DEFAULT_THEME_MOUNTAIN,
            DefaultCoverElement.COVER_DEFAULT_THEME_AURORA,
            DefaultCoverElement.COVER_DEFAULT_THEME_VIETNAM,
            DefaultCoverElement.COVER_DEFAULT_THEME_CHINA,
            DefaultCoverElement.COVER_DEFAULT_THEME_SEA_DIVING
        )
    
    override suspend fun insertTripInfo(tripInfoModel: TripInfoModel): Long {
        if(forceError) {
            throw ExceptionInsertTripInfo()
        }

        val currentId = autoGeneratedId
        tripInfoMap[autoGeneratedId] = tripInfoModel.toTripInfo().copy(tripId = autoGeneratedId)
        autoGeneratedId++
        return currentId
    }

    override suspend fun updateTripInfo(tripInfoModel: TripInfoModel): Long {
        if(forceError || !tripInfoMap.containsKey(tripInfoModel.tripId)) {
            throw ExceptionUpdateTripInfo()
        }

        tripInfoMap[tripInfoModel.tripId] = tripInfoModel.toTripInfo()
        return 1L
    }

    override suspend fun deleteTripInfo(tripId: Long) {
        if(forceError || !tripInfoMap.containsKey(tripId)) {
            throw ExceptionDeleteTripInfo()
        }

        tripInfoMap.remove(tripId)
    }

    fun reset() {
        tripInfoMap.clear()
        autoGeneratedId = 1L
        forceError = false
    }

    @TestOnly
    fun getTripInfo(tripId: Long): TripInfo? {
        return tripInfoMap[tripId]
    }

    @TestOnly
    fun addTripInfo(tripInfo: TripInfo) {
        tripInfoMap[tripInfo.tripId] = tripInfo
        tripInfosFlow.tryEmit(tripInfoMap.values.toList())
    }

    @TestOnly
    fun updateTripInfo(tripInfo: TripInfo) {
        tripInfoMap[tripInfo.tripId] = tripInfo
        tripInfosFlow.tryEmit(tripInfoMap.values.toList())
    }
}