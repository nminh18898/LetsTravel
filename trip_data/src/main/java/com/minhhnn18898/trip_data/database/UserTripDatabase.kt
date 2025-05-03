package com.minhhnn18898.trip_data.database

import androidx.room.Database
import androidx.room.RoomDatabase
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
import com.minhhnn18898.trip_data.model.expense.DefaultBillOwnerModel
import com.minhhnn18898.trip_data.model.expense.MemberInfoModel
import com.minhhnn18898.trip_data.model.expense.ReceiptModel
import com.minhhnn18898.trip_data.model.expense.ReceiptPayerModel
import com.minhhnn18898.trip_data.model.memories.TripMemoriesConfigModel
import com.minhhnn18898.trip_data.model.memories.TripPhotoModel

import com.minhhnn18898.trip_data.model.plan.AirportInfoModel
import com.minhhnn18898.trip_data.model.plan.FlightInfoModel
import com.minhhnn18898.trip_data.model.plan.HotelInfoModel
import com.minhhnn18898.trip_data.model.plan.TripActivityInfoModel
import com.minhhnn18898.trip_data.model.trip_info.TripInfoModel

@Database(entities = [
    AirportInfoModel::class,
    FlightInfoModel::class,
    TripInfoModel::class,
    HotelInfoModel::class,
    TripActivityInfoModel::class,
    MemberInfoModel::class,
    DefaultBillOwnerModel::class,
    ReceiptModel::class,
    ReceiptPayerModel::class,
    TripPhotoModel::class,
    TripMemoriesConfigModel::class
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

    abstract fun defaultBillOwnerDao(): DefaultBillOwnerDao

    abstract fun receiptDao(): ReceiptDao

    abstract fun tripPhotoDao(): TripPhotoDao

    abstract fun memoriesConfigDao(): TripMemoriesConfigDao
}