package com.minhhnn18898.manage_trip.trip_detail.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.minhhnn18898.manage_trip.trip_detail.data.model.DefaultBillOwnerModel
import kotlinx.coroutines.flow.Flow

@Dao
interface DefaultBillOwnerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(billOwnerInfo: DefaultBillOwnerModel): Long

    @Query("DELETE FROM default_bill_owner_model WHERE trip_id = :tripId")
    suspend fun delete(tripId: Long): Int

    @Query("SELECT * FROM default_bill_owner_model WHERE trip_id=:tripId")
    fun getBillOwnerStream(tripId: Long): Flow<DefaultBillOwnerModel?>

    @Query("SELECT * FROM default_bill_owner_model WHERE trip_id=:tripId")
    fun getBillOwner(tripId: Long): DefaultBillOwnerModel?
}