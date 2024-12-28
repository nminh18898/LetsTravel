package com.minhhnn18898.manage_trip.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.DefaultBillOwnerDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.MemberInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.expense.ReceiptDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.plan.ActivityInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.plan.AirportInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.plan.FlightInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.plan.HotelInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.DefaultBillOwnerModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.MemberInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.expense.ReceiptPayerModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.AirportInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.FlightInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.HotelInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.plan.TripActivityInfoModel
import com.minhhnn18898.manage_trip.trip_info.data.dao.TripInfoDao
import com.minhhnn18898.manage_trip.trip_info.data.model.TripInfoModel

@Database(entities = [
    AirportInfoModel::class,
    FlightInfoModel::class,
    TripInfoModel::class,
    HotelInfoModel::class,
    TripActivityInfoModel::class,
    MemberInfoModel::class,
    DefaultBillOwnerModel::class,
    ReceiptModel::class,
    ReceiptPayerModel::class
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
}