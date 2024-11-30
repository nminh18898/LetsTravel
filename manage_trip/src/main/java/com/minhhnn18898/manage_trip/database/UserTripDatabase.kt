package com.minhhnn18898.manage_trip.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.minhhnn18898.manage_trip.trip_detail.data.dao.ActivityInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.AirportInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.FlightInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.HotelInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.MemberInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.MemberInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfoModel
import com.minhhnn18898.manage_trip.trip_info.data.dao.TripInfoDao
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel

@Database(entities = [
    AirportInfoModel::class,
    FlightInfoModel::class,
    TripInfoModel::class,
    HotelInfoModel::class,
    TripActivityInfoModel::class,
    MemberInfoModel::class
],
    version = 1,
    exportSchema = false
)
abstract class UserTripDatabase : RoomDatabase() {

    abstract fun tripInfoDao(): TripInfoDao

    abstract fun flightInfoDao(): FlightInfoDao

    abstract fun airportInfoDao(): AirportInfoDao

    abstract fun hotelInfoDao(): HotelInfoDao

    abstract fun activityInfoDao(): ActivityInfoDao

    abstract fun memberInfoDao(): MemberInfoDao
}