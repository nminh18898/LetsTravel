package com.minhhnn18898.letstravel.app.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.minhhnn18898.letstravel.tripdetail.data.dao.AirportInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.dao.FlightInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.dao.HotelInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfoModel
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfoModel
import com.minhhnn18898.letstravel.tripdetail.data.model.HotelInfoModel
import com.minhhnn18898.letstravel.tripinfo.data.dao.TripInfoDao
import com.minhhnn18898.letstravel.tripinfo.data.model.TripInfoModel

@Database(entities = [
        AirportInfoModel::class,
        FlightInfoModel::class,
        TripInfoModel::class,
        HotelInfoModel::class
    ],
        version = 1,
        exportSchema = false)
abstract class UserTripDatabase : RoomDatabase() {

    abstract fun tripInfoDao(): TripInfoDao

    abstract fun flightInfoDao(): FlightInfoDao

    abstract fun airportInfoDao(): AirportInfoDao

    abstract fun hotelInfoDao(): HotelInfoDao
}