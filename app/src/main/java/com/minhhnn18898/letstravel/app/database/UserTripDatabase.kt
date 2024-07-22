package com.minhhnn18898.letstravel.app.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.minhhnn18898.letstravel.tripdetail.data.dao.AirportInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.dao.FlightInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfoModel
import com.minhhnn18898.letstravel.tripinfo.data.dao.TripInfoDao
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfo

@Database(entities = [
        AirportInfo::class,
        FlightInfoModel::class,
        TripInfo::class,
    ],
        version = 1,
        exportSchema = false)
abstract class UserTripDatabase : RoomDatabase() {

    abstract fun tripInfoDao(): TripInfoDao

    abstract fun flightInfoDao(): FlightInfoDao

    abstract fun airportInfoDao(): AirportInfoDao

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