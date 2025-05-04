package com.minhhnn18898.trip_data.repo.plan

import com.minhhnn18898.trip_data.model.plan.AirportInfo
import com.minhhnn18898.trip_data.model.plan.FlightInfo
import com.minhhnn18898.trip_data.model.plan.FlightWithAirportInfo
import com.minhhnn18898.trip_data.model.plan.HotelInfo
import com.minhhnn18898.trip_data.model.plan.TripActivityInfo
import kotlinx.coroutines.flow.Flow

interface TripDetailRepository {
    // Flight info
    fun getListFlightInfo(tripId: Long): Flow<List<FlightWithAirportInfo>>

    fun getFlightInfo(flightId: Long): Flow<FlightWithAirportInfo?>

    suspend fun insertFlightInfo(
        tripId: Long,
        flightInfo: FlightInfo,
        departAirport: AirportInfo,
        destinationAirport: AirportInfo
    ): Long

    suspend fun updateFlightInfo(
        tripId: Long,
        flightInfo: FlightInfo,
        departAirport: AirportInfo,
        destinationAirport: AirportInfo
    )

    suspend fun deleteFlightInfo(flightId: Long)

    // Hotel Info
    fun getAllHotelInfo(tripId: Long): Flow<List<HotelInfo>>

    fun getHotelInfo(hotelId: Long): Flow<HotelInfo?>

    suspend fun insertHotelInfo(tripId: Long, hotelInfo: HotelInfo): Long

    suspend fun updateHotelInfo(tripId: Long, hotelInfo: HotelInfo)

    suspend fun deleteHotelInfo(hotelId: Long)

    // Trip activity info
    fun getAllActivityInfo(tripId: Long): Flow<List<TripActivityInfo>>

    fun getActivityInfo(activityId: Long): Flow<TripActivityInfo?>

    suspend fun insertActivityInfo(tripId: Long, activityInfo: TripActivityInfo): Long

    suspend fun updateActivityInfo(tripId: Long, activityInfo: TripActivityInfo)

    suspend fun deleteActivityInfo(activityId: Long)

}