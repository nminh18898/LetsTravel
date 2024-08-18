package com.minhhnn18898.letstravel.tripinfo.data.repo

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.letstravel.tripinfo.data.dao.TripInfoDao
import com.minhhnn18898.letstravel.tripinfo.data.model.ExceptionInsertTripInfo
import com.minhhnn18898.letstravel.tripinfo.data.model.ExceptionUpdateTripInfo
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfoModel
import com.minhhnn18898.letstravel.tripinfo.data.model.toTripInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripInfoRepository @Inject constructor(
    private val tripInfoDao: TripInfoDao,
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher
) {

    private val defaultCoverIdList = listOf(
        DefaultCoverElement.COVER_DEFAULT_THEME_SUMMER,
        DefaultCoverElement.COVER_DEFAULT_THEME_LONG_TRIP,
        DefaultCoverElement.COVER_DEFAULT_THEME_AROUND_THE_WORLD,
        DefaultCoverElement.COVER_DEFAULT_THEME_NIGHT_DRIVE,
        DefaultCoverElement.COVER_DEFAULT_THEME_SEA,
        DefaultCoverElement.COVER_DEFAULT_THEME_NATURE
    )

    fun getAllTrips(): Flow<List<TripInfo>> {
        return tripInfoDao
            .getAll()
            .map {
                it.toTripInfo()
            }
    }

    fun getTrip(id: Long): Flow<TripInfo> {
        return tripInfoDao
            .getTripInfo(id)
            .map {
                it.toTripInfo()
            }
    }

    fun getListDefaultCoverElements(): List<DefaultCoverElement> = defaultCoverIdList

    suspend fun insertTripInfo(tripInfoModel: TripInfoModel) {
        withContext(ioDispatcher) {
            val resultCode = tripInfoDao.insert(tripInfoModel.copy(tripId = 0L))
            if(resultCode == -1L) {
                throw ExceptionInsertTripInfo()
            }
        }
    }

    suspend fun updateTripInfo(tripInfoModel: TripInfoModel) = withContext(ioDispatcher) {
        val resultCode = tripInfoDao.update(tripInfoModel)
        if(resultCode <= 0) {
            throw ExceptionUpdateTripInfo()
        }
    }
}

private fun List<TripInfoModel>.toTripInfo(): List<TripInfo> {
    return this.map { it.toTripInfo() }
}