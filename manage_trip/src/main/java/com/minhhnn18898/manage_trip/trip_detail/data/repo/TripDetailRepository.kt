package com.minhhnn18898.manage_trip.trip_detail.data.repo

import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
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
    fun getSortedActivityInfo(tripId: Long): Flow<Map<Long?, List<TripActivityInfo>>>

    fun getActivityInfo(activityId: Long): Flow<TripActivityInfo?>

    suspend fun insertActivityInfo(tripId: Long, activityInfo: TripActivityInfo): Long

    suspend fun updateActivityInfo(tripId: Long, activityInfo: TripActivityInfo)

    suspend fun deleteActivityInfo(activityId: Long)

}