package com.minhhnn18898.trip_data.di

import android.content.Context
import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.trip_data.dao.expense.DefaultBillOwnerDao
import com.minhhnn18898.trip_data.dao.expense.MemberInfoDao
import com.minhhnn18898.trip_data.dao.expense.ReceiptDao
import com.minhhnn18898.trip_data.dao.memories.TripMemoriesConfigDao
import com.minhhnn18898.trip_data.dao.memories.TripPhotoDao
import com.minhhnn18898.trip_data.dao.plan.ActivityInfoDao
import com.minhhnn18898.trip_data.dao.plan.AirportInfoDao
import com.minhhnn18898.trip_data.dao.plan.FlightInfoDao
import com.minhhnn18898.trip_data.dao.plan.HotelInfoDao
import com.minhhnn18898.trip_data.dao.trip_info.TripInfoDao
import com.minhhnn18898.trip_data.repo.expense.MemberInfoRepository
import com.minhhnn18898.trip_data.repo.expense.MemberInfoRepositoryImpl
import com.minhhnn18898.trip_data.repo.expense.ReceiptRepository
import com.minhhnn18898.trip_data.repo.expense.ReceiptRepositoryImpl
import com.minhhnn18898.trip_data.repo.memories.MemoriesConfigRepository
import com.minhhnn18898.trip_data.repo.memories.MemoriesConfigRepositoryImpl
import com.minhhnn18898.trip_data.repo.memories.TripPhotoRepository
import com.minhhnn18898.trip_data.repo.memories.TripPhotoRepositoryImpl
import com.minhhnn18898.trip_data.repo.plan.TripDetailRepository
import com.minhhnn18898.trip_data.repo.plan.TripDetailRepositoryImpl
import com.minhhnn18898.trip_data.repo.trip_info.TripInfoRepository
import com.minhhnn18898.trip_data.repo.trip_info.TripInfoRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Provides
    fun provideTripDetailRepository(
        @IODispatcher
        ioDispatcher: CoroutineDispatcher,
        airportInfoDao: AirportInfoDao,
        flightInfoDao: FlightInfoDao,
        hotelInfoDao: HotelInfoDao,
        activityInfoDao: ActivityInfoDao
    ): TripDetailRepository {
        return TripDetailRepositoryImpl(
            ioDispatcher = ioDispatcher,
            airportInfoDao = airportInfoDao,
            flightInfoDao = flightInfoDao,
            hotelInfoDao = hotelInfoDao,
            activityInfoDao = activityInfoDao,
        )
    }

    @Provides
    fun provideMemberInfoRepository(
        @IODispatcher
        ioDispatcher: CoroutineDispatcher,
        memberInfoDao: MemberInfoDao,
        defaultBillOwnerDao: DefaultBillOwnerDao
    ): MemberInfoRepository {
        return MemberInfoRepositoryImpl(
            ioDispatcher = ioDispatcher,
            memberInfoDao = memberInfoDao,
            defaultBillOwnerDao = defaultBillOwnerDao
        )
    }

    @Provides
    fun provideReceiptRepository(
        @IODispatcher
        ioDispatcher: CoroutineDispatcher,
        receiptDao: ReceiptDao
    ): ReceiptRepository {
        return ReceiptRepositoryImpl(
            ioDispatcher = ioDispatcher,
            receiptDao = receiptDao
        )
    }

    @Provides
    fun provideTripPhotoRepository(
        @IODispatcher
        ioDispatcher: CoroutineDispatcher,
        tripPhotoDao: TripPhotoDao,
        @ApplicationContext appContext: Context
    ): TripPhotoRepository {
        return TripPhotoRepositoryImpl(
            ioDispatcher = ioDispatcher,
            photoInfoDao = tripPhotoDao,
            context = appContext
        )
    }

    @Provides
    fun provideMemoriesConfigRepository(
        @IODispatcher
        ioDispatcher: CoroutineDispatcher,
        memoriesConfigDao: TripMemoriesConfigDao
    ): MemoriesConfigRepository {
        return MemoriesConfigRepositoryImpl(
            ioDispatcher = ioDispatcher,
            configInfoDao = memoriesConfigDao
        )
    }
}