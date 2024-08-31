package com.minhhnn18898.tripdetail.di

import android.content.Context
import androidx.room.Room
import com.minhhnn18898.tripdetail.database.UserTripDatabase
import com.minhhnn18898.tripdetail.tripdetail.data.dao.ActivityInfoDao
import com.minhhnn18898.tripdetail.tripdetail.data.dao.AirportInfoDao
import com.minhhnn18898.tripdetail.tripdetail.data.dao.FlightInfoDao
import com.minhhnn18898.tripdetail.tripdetail.data.dao.HotelInfoDao
import com.minhhnn18898.tripdetail.tripinfo.data.dao.TripInfoDao
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
}