package com.minhhnn18898.letstravel.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.minhhnn18898.letstravel.data.dao.AirportInfoDao
import com.minhhnn18898.letstravel.data.dao.FlightInfoDao
import com.minhhnn18898.letstravel.data.dao.FlightWithAirportInfoMappingDao
import com.minhhnn18898.letstravel.data.model.AirportInfo
import com.minhhnn18898.letstravel.data.model.FlightInfo
import com.minhhnn18898.letstravel.data.model.FlightWithAirportInfoMapping
import com.minhhnn18898.letstravel.tripinfo.data.dao.TripInfoDao
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo

@Database(entities = [
        AirportInfo::class,
        FlightInfo::class,
        TripInfo::class,
        FlightWithAirportInfoMapping::class
    ],
        version = 1,
        exportSchema = false)
abstract class UserTripDatabase : RoomDatabase() {

    abstract fun tripInfoDao(): TripInfoDao

    abstract fun flightInfoDao(): FlightInfoDao

    abstract fun airportInfoDao(): AirportInfoDao

    abstract fun flightWithAirportInfoMappingDao(): FlightWithAirportInfoMappingDao

    companion object {
        @Volatile
        private var Instance: UserTripDatabase? = null

        fun getDatabase(context: Context): UserTripDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, UserTripDatabase::class.java, "user_trip_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}