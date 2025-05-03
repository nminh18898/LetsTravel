package com.minhhnn18898.trip_data.repo.trip_info

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.trip_data.dao.trip_info.TripInfoDao
import com.minhhnn18898.trip_data.model.trip_info.ExceptionDeleteTripInfo
import com.minhhnn18898.trip_data.model.trip_info.ExceptionInsertTripInfo
import com.minhhnn18898.trip_data.model.trip_info.ExceptionUpdateTripInfo
import com.minhhnn18898.trip_data.model.trip_info.TripInfo
import com.minhhnn18898.trip_data.model.trip_info.TripInfoModel
import com.minhhnn18898.trip_data.model.trip_info.toTripInfo
import com.minhhnn18898.trip_data.model.trip_info.toTripInfoModel
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

    override suspend fun insertTripInfo(tripInfo: TripInfo): Long = withContext(ioDispatcher) {
        val resultCode = tripInfoDao.insert(tripInfo.toTripInfoModel().copy(tripId = 0L))
        if(resultCode == -1L) {
            throw ExceptionInsertTripInfo()
        }
        resultCode
    }

    override suspend fun updateTripInfo(tripInfo: TripInfo): Long = withContext(ioDispatcher) {
        val resultCode = tripInfoDao.update(tripInfo.toTripInfoModel())
        if(resultCode <= 0) {
            throw ExceptionUpdateTripInfo()
        }
        tripInfo.tripId
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