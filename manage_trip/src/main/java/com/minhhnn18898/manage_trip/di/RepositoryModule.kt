package com.minhhnn18898.manage_trip.di

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.manage_trip.trip_info.data.dao.TripInfoDao
import com.minhhnn18898.manage_trip.trip_info.data.repo.TripInfoRepository
import com.minhhnn18898.manage_trip.trip_info.data.repo.TripInfoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    fun provideTripInfoRepository(
        tripInfoDao: TripInfoDao,
        @IODispatcher
        ioDispatcher: CoroutineDispatcher
    ): TripInfoRepository {
        return TripInfoRepositoryImpl(tripInfoDao, ioDispatcher)
    }
}