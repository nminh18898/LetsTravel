package com.minhhnn18898.letstravel.tripdetail.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("airport_info")
data class AirportInfoModel(
    @PrimaryKey
    @ColumnInfo("airport_code")
    val code: String,
    val city: String,
    @ColumnInfo("airport_name")
    val airportName: String = ""
)