package com.minhhnn18898.manage_trip.di

import android.content.Context
import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.core.utils.BaseDateTimeFormatterImpl
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.DefaultBillOwnerDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.MemberInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.ReceiptDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.memories.TripMemoriesConfigDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.memories.TripPhotoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.plan.ActivityInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.plan.AirportInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.plan.FlightInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.plan.HotelInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.MemberInfoRepository
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.MemberInfoRepositoryImpl
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.ReceiptRepository
import com.minhhnn18898.manage_trip.trip_detail.data.repo.expense.ReceiptRepositoryImpl
import com.minhhnn18898.manage_trip.trip_detail.data.repo.memories.MemoriesConfigRepository
import com.minhhnn18898.manage_trip.trip_detail.data.repo.memories.MemoriesConfigRepositoryImpl
import com.minhhnn18898.manage_trip.trip_detail.data.repo.memories.TripPhotoRepository
import com.minhhnn18898.manage_trip.trip_detail.data.repo.memories.TripPhotoRepositoryImpl
import com.minhhnn18898.manage_trip.trip_detail.data.repo.plan.TripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.repo.plan.TripDetailRepositoryImpl
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatterImpl
import com.minhhnn18898.manage_trip.trip_info.data.dao.TripInfoDao
import com.minhhnn18898.manage_trip.trip_info.data.repo.TripInfoRepository
import com.minhhnn18898.manage_trip.trip_info.data.repo.TripInfoRepositoryImpl
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
            dateTimeFormatter = TripDetailDateTimeFormatterImpl(BaseDateTimeFormatterImpl())
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