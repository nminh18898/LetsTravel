package com.minhhnn18898.manage_trip.trip_detail.data

import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toFlightInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionDeleteFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionInsertFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionUpdateFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.jetbrains.annotations.TestOnly

class FakeTripDetailRepository: TripDetailRepository {

    private var autoGeneratedId = 1L

    private val flightInfoModelMap: LinkedHashMap<Long, FlightInfoModel> = LinkedHashMap()
    private val airportInfoMap: LinkedHashMap<String, AirportInfo> = LinkedHashMap()

    private val flightInfosFlow: MutableSharedFlow<List<FlightInfoModel>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    var forceError = false

    override fun getListFlightInfo(tripId: Long): Flow<List<FlightWithAirportInfo>> {
        if(forceError) {
            throw Exception("Internal Error. Can not get trip info")
        }

        if(flightInfoModelMap.isNotEmpty()) {
            return flightInfosFlow.map {
                it.toListFlightAirportInfo()
            }
        }

        return flow { emit(emptyList()) }
    }

    override suspend fun getFlightInfo(flightId: Long): Flow<FlightWithAirportInfo?> {
        if(forceError) {
            throw Exception("Internal Error. Can not get flight info")
        }

        if(!flightInfoModelMap.containsKey(flightId)) {
            return flow { emit(null) }
        }

        return flightInfosFlow.map {
            val flightInfoModel = flightInfoModelMap[flightId]
            if(flightInfoModel != null) {
                FlightWithAirportInfo(
                    flightInfo = flightInfoModel.toFlightInfo(),
                    departAirport = airportInfoMap[flightInfoModel.departAirportCode] ?: AirportInfo(),
                    destinationAirport = airportInfoMap[flightInfoModel.destinationAirportCode] ?: AirportInfo()
                )
            } else {
                null
            }
        }
    }

    override suspend fun insertFlightInfo(
        tripId: Long,
        flightInfo: FlightInfo,
        departAirport: AirportInfo,
        destinationAirport: AirportInfo
    ): Long {
        if(forceError) {
            throw ExceptionInsertFlightInfo()
        }

        val currentId = autoGeneratedId

        flightInfoModelMap[currentId] = flightInfo.toFlightInfoModel(
            tripId = tripId,
            departAirportCode = departAirport.code,
            destinationAirportCode = destinationAirport.code
        )
        airportInfoMap[departAirport.code] = departAirport
        airportInfoMap[destinationAirport.code] = destinationAirport

        flightInfosFlow.tryEmit(flightInfoModelMap.values.toList())
        autoGeneratedId++

        return currentId
    }

    override suspend fun updateFlightInfo(tripId: Long, flightInfo: FlightInfo, departAirport: AirportInfo, destinationAirport: AirportInfo) {
        if(forceError || !flightInfoModelMap.containsKey(flightInfo.flightId)) {
            throw ExceptionUpdateFlightInfo()
        }

        flightInfoModelMap[flightInfo.flightId] = flightInfo.toFlightInfoModel(
            tripId = tripId,
            departAirportCode = departAirport.code,
            destinationAirportCode = destinationAirport.code
        )
        airportInfoMap[departAirport.code] = departAirport
        airportInfoMap[destinationAirport.code] = destinationAirport

        flightInfosFlow.tryEmit(flightInfoModelMap.values.toList())
    }

    override suspend fun deleteFlightInfo(flightId: Long) {
        if(forceError || !flightInfoModelMap.containsKey(flightId)) {
            throw ExceptionDeleteFlightInfo()
        }

        flightInfoModelMap.remove(flightId)
    }

    override fun getAllHotelInfo(tripId: Long): Flow<List<HotelInfo>> {
        TODO("Not yet implemented")
    }

    override suspend fun getHotelInfo(hotelId: Long): Flow<HotelInfo?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertHotelInfo(tripId: Long, hotelInfo: HotelInfo): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateHotelInfo(tripId: Long, hotelInfo: HotelInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteHotelInfo(hotelId: Long) {
        TODO("Not yet implemented")
    }

    override fun getSortedActivityInfo(tripId: Long): Flow<Map<Long?, List<TripActivityInfo>>> {
        TODO("Not yet implemented")
    }

    override fun getActivityInfo(activityId: Long): Flow<TripActivityInfo?> {
        TODO("Not yet implemented")
    }

    override suspend fun insertActivityInfo(tripId: Long, activityInfo: TripActivityInfo): Long {
        TODO("Not yet implemented")
    }

    override suspend fun updateActivityInfo(tripId: Long, activityInfo: TripActivityInfo) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteActivityInfo(activityId: Long) {
        TODO("Not yet implemented")
    }

    fun reset() {
        flightInfoModelMap.clear()
        airportInfoMap.clear()
        autoGeneratedId = 1L
        forceError = false
    }

    private fun List<FlightInfoModel>.toListFlightAirportInfo(): List<FlightWithAirportInfo> {
        return this.map {
            FlightWithAirportInfo(
                flightInfo = it.toFlightInfo(),
                departAirport = airportInfoMap[it.departAirportCode] ?: AirportInfo(),
                destinationAirport = airportInfoMap[it.destinationAirportCode] ?: AirportInfo()
            )
        }
    }

    @TestOnly
    fun getFlightAirportInfo(flightId: Long): FlightWithAirportInfo? {
        val flightInfoModel = flightInfoModelMap[flightId]

        return flightInfoModel?.let {
            FlightWithAirportInfo(
                flightInfo = it.toFlightInfo(),
                departAirport = airportInfoMap[it.departAirportCode] ?: AirportInfo(),
                destinationAirport = airportInfoMap[it.destinationAirportCode] ?: AirportInfo()
            )
        }
    }

    @TestOnly
    fun addFlightAirportInfo(tripId: Long, flightInfo: FlightInfo, departAirport: AirportInfo, destinationAirport: AirportInfo) {
        flightInfoModelMap[flightInfo.flightId] = flightInfo.toFlightInfoModel(
            tripId = tripId,
            departAirportCode = departAirport.code,
            destinationAirportCode = destinationAirport.code
        )
        addAirportInfo(departAirport)
        addAirportInfo(destinationAirport)

        flightInfosFlow.tryEmit(flightInfoModelMap.values.toList())
    }

    @TestOnly
    fun updateFlightAirportInfo(tripId: Long, flightInfo: FlightInfo, departAirport: AirportInfo, destinationAirport: AirportInfo) {
        flightInfoModelMap[flightInfo.flightId] = flightInfo.toFlightInfoModel(
            tripId = tripId,
            departAirportCode = departAirport.code,
            destinationAirportCode = destinationAirport.code
        )
        addAirportInfo(departAirport)
        addAirportInfo(destinationAirport)

        flightInfosFlow.tryEmit(flightInfoModelMap.values.toList())
    }

    @TestOnly
    fun getAirportInfo(airportCode: String): AirportInfo? {
        return airportInfoMap[airportCode]
    }

    @TestOnly
    fun addAirportInfo(airportInfo: AirportInfo) {
        airportInfoMap[airportInfo.code] = airportInfo
    }
}