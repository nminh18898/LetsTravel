package com.minhhnn18898.manage_trip.tripinfo.data.repo

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.manage_trip.tripinfo.data.dao.TripInfoDao
import com.minhhnn18898.manage_trip.tripinfo.data.model.ExceptionDeleteTripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.ExceptionInsertTripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.ExceptionUpdateTripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfo
import com.minhhnn18898.manage_trip.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.manage_trip.tripinfo.data.model.toTripInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripInfoRepositoryImpl @Inject constructor(
    private val tripInfoDao: TripInfoDao,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher
) : TripInfoRepository {

    private val defaultCoverIdList = listOf(
        DefaultCoverElement.COVER_DEFAULT_THEME_SPRING,
        DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER,
        DefaultCoverElement.COVER_DEFAULT_THEME_AUTUMN,
        DefaultCoverElement.COVER_DEFAULT_THEME_WINTER,
        DefaultCoverElement.COVER_DEFAULT_THEME_BEACH,
        DefaultCoverElement.COVER_DEFAULT_THEME_MOUNTAIN,
        DefaultCoverElement.COVER_DEFAULT_THEME_AURORA,
        DefaultCoverElement.COVER_DEFAULT_THEME_VIETNAM,
        DefaultCoverElement.COVER_DEFAULT_THEME_CHINA,
        DefaultCoverElement.COVER_DEFAULT_THEME_SEA_DIVING,
    )

    override fun getAllTrips(): Flow<List<TripInfo>> {
        return tripInfoDao
            .getAll()
            .map {
                it.toTripInfo()
            }
    }

    override fun getTrip(id: Long): Flow<TripInfo?> {
        return tripInfoDao
            .getTripInfo(id)
            .map {
                it?.toTripInfo()
            }
    }

    override fun getListDefaultCoverElements(): List<DefaultCoverElement> = defaultCoverIdList

    override suspend fun insertTripInfo(tripInfoModel: TripInfoModel): Long = withContext(ioDispatcher) {
        val resultCode = tripInfoDao.insert(tripInfoModel.copy(tripId = 0L))
        if(resultCode == -1L) {
            throw ExceptionInsertTripInfo()
        }
        resultCode
    }

    override suspend fun updateTripInfo(tripInfoModel: TripInfoModel): Long = withContext(ioDispatcher) {
        val resultCode = tripInfoDao.update(tripInfoModel)
        if(resultCode <= 0) {
            throw ExceptionUpdateTripInfo()
        }
        tripInfoModel.tripId
    }

    override suspend fun deleteTripInfo(tripId: Long) = withContext(ioDispatcher) {
        val result = tripInfoDao.delete(tripId)
        if(result <= 0) {
            throw ExceptionDeleteTripInfo()
        }
    }
}

private fun List<TripInfoModel>.toTripInfo(): List<TripInfo> {
    return this.map { it.toTripInfo() }
}