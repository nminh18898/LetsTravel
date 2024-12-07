package com.minhhnn18898.manage_trip.di

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.core.utils.BaseDateTimeFormatterImpl
import com.minhhnn18898.manage_trip.trip_detail.data.dao.ActivityInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.AirportInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.DefaultBillOwnerDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.FlightInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.HotelInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.MemberInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.repo.MemberInfoRepository
import com.minhhnn18898.manage_trip.trip_detail.data.repo.MemberInfoRepositoryImpl
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepository
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepositoryImpl
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatterImpl
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
}