package com.minhhnn18898.trip_data.test_helper

import com.minhhnn18898.trip_data.model.trip_info.ExceptionDeleteTripInfo
import com.minhhnn18898.trip_data.model.trip_info.ExceptionInsertTripInfo
import com.minhhnn18898.trip_data.model.trip_info.ExceptionUpdateTripInfo
import com.minhhnn18898.trip_data.model.trip_info.TripInfo
import com.minhhnn18898.trip_data.repo.trip_info.DefaultCoverElement
import com.minhhnn18898.trip_data.repo.trip_info.TripInfoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class FakeTripInfoRepository: TripInfoRepository {

    private var autoGeneratedId = 1L

    private val _savedTrips = MutableStateFlow(LinkedHashMap<Long, TripInfo>())
    private val observableTrips: Flow<List<TripInfo>> = _savedTrips.map {
        it.values.toList()
    }

    var forceError = false

    override fun getAllTrips(): Flow<List<TripInfo>> {
        if(forceError) {
            return flow { throw Exception() }
        }

        return observableTrips
    }

    override fun getTrip(id: Long): Flow<TripInfo?> {
        return observableTrips.map { trips ->
            return@map trips.firstOrNull { it.tripId == id }
        }
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
    
    override suspend fun insertTripInfo(tripInfo: TripInfo): Long {
        if(forceError) {
            throw ExceptionInsertTripInfo()
        }

        val currentId = autoGeneratedId
        saveTrip(tripInfo.copy(tripId = autoGeneratedId))
        autoGeneratedId++

        return currentId
    }

    override suspend fun updateTripInfo(tripInfo: TripInfo): Long {
        if(forceError || !_savedTrips.value.containsKey(tripInfo.tripId)) {
            throw ExceptionUpdateTripInfo()
        }

        saveTrip(tripInfo)

        return tripInfo.tripId
    }

    override suspend fun deleteTripInfo(tripId: Long) {
        if(forceError || !_savedTrips.value.containsKey(tripId)) {
            throw ExceptionDeleteTripInfo()
        }

        _savedTrips.update { listTrip ->
            val newTrips = LinkedHashMap<Long, TripInfo>(listTrip)
            newTrips.remove(tripId)
            newTrips
        }
    }

    fun reset() {
        _savedTrips.update { LinkedHashMap() }
        autoGeneratedId = 1L
        forceError = false
    }

    @TestOnly
    fun getTripInfo(tripId: Long): TripInfo? {
        return _savedTrips.value[tripId]
    }

    @TestOnly
    fun addTrip(vararg tripInfos: TripInfo) {
        _savedTrips.update { oldTrips ->
            val newTrips = LinkedHashMap<Long, TripInfo>(oldTrips)
            for (trip in tripInfos) {
                newTrips[trip.tripId] = trip
            }
            newTrips
        }
    }

    private fun saveTrip(tripInfo: TripInfo) {
        _savedTrips.update { listTrip ->
            val newTrips = LinkedHashMap<Long, TripInfo>(listTrip)
            newTrips[tripInfo.tripId] = tripInfo
            newTrips
        }
    }
}