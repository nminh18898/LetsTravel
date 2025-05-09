package com.minhhnn18898.trip_data.di

import android.content.Context
import androidx.room.Room
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
import com.minhhnn18898.trip_data.database.UserTripDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): UserTripDatabase {
        return Room.databaseBuilder(
            appContext,
            UserTripDatabase::class.java,
            "user_trip_database.db")
            .build()
    }

    @Provides
    fun provideTripInfoDao(database: UserTripDatabase): TripInfoDao {
        return database.tripInfoDao()
    }

    @Provides
    fun provideAirportInfoDao(database: UserTripDatabase): AirportInfoDao {
        return database.airportInfoDao()
    }

    @Provides
    fun provideFlightInfoDao(database: UserTripDatabase): FlightInfoDao {
        return database.flightInfoDao()
    }

    @Provides
    fun provideHotelInfoDao(database: UserTripDatabase): HotelInfoDao {
        return database.hotelInfoDao()
    }

    @Provides
    fun provideActivityInfoDao(database: UserTripDatabase): ActivityInfoDao {
        return database.activityInfoDao()
    }

    @Provides
    fun provideMemberInfoDao(database: UserTripDatabase): MemberInfoDao {
        return database.memberInfoDao()
    }

    @Provides
    fun provideDefaultBillOwnerInfoDao(database: UserTripDatabase): DefaultBillOwnerDao {
        return database.defaultBillOwnerDao()
    }

    @Provides
    fun provideReceiptDao(database: UserTripDatabase): ReceiptDao {
        return database.receiptDao()
    }

    @Provides
    fun provideTripPhotoDao(database: UserTripDatabase): TripPhotoDao {
        return database.tripPhotoDao()
    }

    @Provides
    fun provideMemoriesConfigDao(database: UserTripDatabase): TripMemoriesConfigDao {
        return database.memoriesConfigDao()
    }
}