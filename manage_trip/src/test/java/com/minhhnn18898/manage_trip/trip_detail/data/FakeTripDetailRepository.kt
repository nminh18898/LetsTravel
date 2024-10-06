package com.minhhnn18898.manage_trip.trip_detail.data

import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.toFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toFlightInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.toHotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toHotelInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.toTripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toTripActivityModel
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionDeleteFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionDeleteHotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionDeleteTripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionInsertFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionInsertHotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionInsertTripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionUpdateFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionUpdateHotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.ExceptionUpdateTripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.repo.TripDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.jetbrains.annotations.TestOnly

class FakeTripDetailRepository: TripDetailRepository {

    private var autoGeneratedId = 1L

    var forceError = false

    private val _savedFlightInfos = MutableStateFlow(LinkedHashMap<Long, FlightInfoModel>())
    private val _savedAirportInfos = MutableStateFlow(LinkedHashMap<String, AirportInfo>())
    private val observableFlightInfo: Flow<List<FlightInfoModel>> = _savedFlightInfos.map {
        it.values.toList()
    }

    private val _savedHotels = MutableStateFlow(LinkedHashMap<Long, HotelInfoModel>())
    private val observableHotels: Flow<List<HotelInfoModel>> = _savedHotels.map {
        it.values.toList()
    }

    private val _savedActivities = MutableStateFlow(LinkedHashMap<Long, TripActivityInfoModel>())
    private val observableActivities: Flow<List<TripActivityInfoModel>> = _savedActivities.map {
        it.values.toList()
    }

    override fun getListFlightInfo(tripId: Long): Flow<List<FlightWithAirportInfo>> {
        if(forceError) {
            return flow { throw Exception() }
        }

        return observableFlightInfo.map {
            it.toListFlightAirportInfo()
        }
    }

    override fun getFlightInfo(flightId: Long): Flow<FlightWithAirportInfo?> {
        return observableFlightInfo.map { flights ->
            return@map flights.firstOrNull { it.flightId == flightId }?.toFlightAirportInfo()
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

        saveAirportInfo(departAirport)
        saveAirportInfo(destinationAirport)

        saveFlightInfo(
            flightInfo.toFlightInfoModel(
                tripId = tripId,
                departAirportCode = departAirport.code,
                destinationAirportCode = destinationAirport.code
            )
            .copy(flightId = currentId))

        autoGeneratedId++

        return currentId
    }

    override suspend fun updateFlightInfo(tripId: Long, flightInfo: FlightInfo, departAirport: AirportInfo, destinationAirport: AirportInfo) {
        if(forceError || !_savedFlightInfos.value.containsKey(flightInfo.flightId)) {
            throw ExceptionUpdateFlightInfo()
        }

        saveAirportInfo(departAirport)
        saveAirportInfo(destinationAirport)
        saveFlightInfo(
            flightInfo.toFlightInfoModel(
                tripId = tripId,
                departAirportCode = departAirport.code,
                destinationAirportCode = destinationAirport.code
            )
        )
    }

    override suspend fun deleteFlightInfo(flightId: Long) {
        if(forceError || !_savedFlightInfos.value.containsKey(flightId)) {
            throw ExceptionDeleteFlightInfo()
        }

        _savedFlightInfos.update { listFlight ->
            val newFlights = LinkedHashMap<Long, FlightInfoModel>(listFlight)
            newFlights.remove(flightId)
            newFlights
        }
    }

    override fun getAllHotelInfo(tripId: Long): Flow<List<HotelInfo>> {
        if(forceError) {
            return flow { throw Exception() }
        }

        return observableHotels.map { it.toHotelInfo() }
    }

    override fun getHotelInfo(hotelId: Long): Flow<HotelInfo?> {
        return observableHotels.map { hotels ->
            return@map hotels.firstOrNull { it.hotelId == hotelId }?.toHotelInfo()
        }
    }

    override suspend fun insertHotelInfo(tripId: Long, hotelInfo: HotelInfo): Long {
        if(forceError) {
            throw ExceptionInsertHotelInfo()
        }

        val currentId = autoGeneratedId
        saveHotel(hotelInfo.toHotelInfoModel(tripId = tripId).copy(hotelId = currentId))
        autoGeneratedId++

        return currentId
    }

    override suspend fun updateHotelInfo(tripId: Long, hotelInfo: HotelInfo) {
        if(forceError || !_savedHotels.value.containsKey(hotelInfo.hotelId)) {
            throw ExceptionUpdateHotelInfo()
        }

        saveHotel(hotelInfo.toHotelInfoModel(tripId))
    }

    override suspend fun deleteHotelInfo(hotelId: Long) {
        if(forceError || !_savedHotels.value.containsKey(hotelId)) {
            throw ExceptionDeleteHotelInfo()
        }

        _savedHotels.update { listHotel ->
            val newHotels = LinkedHashMap<Long, HotelInfoModel>(listHotel)
            newHotels.remove(hotelId)
            newHotels
        }
    }

    override fun getSortedActivityInfo(tripId: Long): Flow<Map<Long?, List<TripActivityInfo>>> {
        if(forceError) {
            return flow { throw Exception() }
        }

        return observableActivities.map { listActivity ->
            listActivity
                .toTripActivityInfo()
                .sortedBy {
                    it.timeFrom
                }
                .groupBy {
                    if (it.timeFrom != null) ((it.timeFrom as Long / 1_000_000f).toInt() * 1_000_000L) else null
                }
        }
    }

    override fun getActivityInfo(activityId: Long): Flow<TripActivityInfo?> {
        return observableActivities.map { activities ->
            return@map activities.firstOrNull { it.activityId == activityId }?.toTripActivityInfo()
        }
    }

    override suspend fun insertActivityInfo(tripId: Long, activityInfo: TripActivityInfo): Long {
        if(forceError) {
            throw ExceptionInsertTripActivityInfo()
        }

        val currentId = autoGeneratedId
        saveActivityInfo(activityInfo.toTripActivityModel(tripId = tripId).copy(activityId = currentId))
        autoGeneratedId++

        return currentId
    }

    override suspend fun updateActivityInfo(tripId: Long, activityInfo: TripActivityInfo) {
        if(forceError || !_savedActivities.value.containsKey(activityInfo.activityId)) {
            throw ExceptionUpdateTripActivityInfo()
        }

        saveActivityInfo(activityInfo.toTripActivityModel(tripId))
    }

    override suspend fun deleteActivityInfo(activityId: Long) {
        if(forceError || !_savedActivities.value.containsKey(activityId)) {
            throw ExceptionDeleteTripActivityInfo()
        }

        _savedActivities.update { listActivity ->
            val newActivities = LinkedHashMap<Long, TripActivityInfoModel>(listActivity)
            newActivities.remove(activityId)
            newActivities
        }
    }

    fun reset() {
        autoGeneratedId = 1L
        forceError = false
        _savedHotels.update { LinkedHashMap() }
        _savedActivities.update { LinkedHashMap() }
        _savedAirportInfos.update { LinkedHashMap() }
        _savedFlightInfos.update { LinkedHashMap() }
    }

    private fun List<FlightInfoModel>.toListFlightAirportInfo(): List<FlightWithAirportInfo> {
        return this.map {
            FlightWithAirportInfo(
                flightInfo = it.toFlightInfo(),
                departAirport = _savedAirportInfos.value[it.departAirportCode] ?: AirportInfo(),
                destinationAirport =  _savedAirportInfos.value[it.destinationAirportCode] ?: AirportInfo()
            )
        }
    }

    private fun FlightInfoModel.toFlightAirportInfo(): FlightWithAirportInfo {
        return FlightWithAirportInfo(
            flightInfo = this.toFlightInfo(),
            departAirport = _savedAirportInfos.value[this.departAirportCode] ?: AirportInfo(),
            destinationAirport =  _savedAirportInfos.value[this.destinationAirportCode] ?: AirportInfo()
        )
    }

    @TestOnly
    fun upsertFlightInfo(tripId: Long, listFlightInfo: List<FlightWithAirportInfo>) {
        listFlightInfo.forEach {
            upsertFlightInfo(
                tripId = tripId,
                flightInfo = it.flightInfo,
                departAirport = it.departAirport,
                destinationAirport = it.destinationAirport
            )
        }
    }

    @TestOnly
    fun upsertFlightInfo(tripId: Long, flightInfo: FlightInfo, departAirport: AirportInfo, destinationAirport: AirportInfo) {
        upsertAirportInfo(departAirport)
        upsertAirportInfo(destinationAirport)

        _savedFlightInfos.update { oldFlights ->
            val newFlights = LinkedHashMap<Long, FlightInfoModel>(oldFlights)
            newFlights[flightInfo.flightId] = flightInfo.toFlightInfoModel(
                tripId = tripId,
                departAirportCode = departAirport.code,
                destinationAirportCode = destinationAirport.code
            )
            newFlights
        }
    }

    @TestOnly
    fun upsertAirportInfo(airportInfo: AirportInfo) {
        _savedAirportInfos.update { oldAirports ->
            val newAirports = LinkedHashMap<String, AirportInfo>(oldAirports)
            newAirports[airportInfo.code] = airportInfo
            newAirports
        }
    }

    @TestOnly
    fun getFlightAirportInfo(flightId: Long): FlightWithAirportInfo? {
        val flightInfoModel = _savedFlightInfos.value[flightId]

        return flightInfoModel?.let {
            FlightWithAirportInfo(
                flightInfo = it.toFlightInfo(),
                departAirport = _savedAirportInfos.value[it.departAirportCode] ?: AirportInfo(),
                destinationAirport = _savedAirportInfos.value[it.destinationAirportCode] ?: AirportInfo()
            )
        }
    }

    @TestOnly
    fun getAirportInfo(airportCode: String): AirportInfo? {
        return _savedAirportInfos.value[airportCode]
    }

    @TestOnly
    fun getHotelInfoForTesting(hotelId: Long): HotelInfo? {
        return _savedHotels.value[hotelId]?.toHotelInfo()
    }

    @TestOnly
    fun upsertHotelInfo(tripId: Long, vararg hotels: HotelInfo) {
        _savedHotels.update { oldHotels ->
            val newHotels = LinkedHashMap<Long, HotelInfoModel>(oldHotels)
            for (hotel in hotels) {
                newHotels[hotel.hotelId] = hotel.toHotelInfoModel(tripId)
            }
            newHotels
        }
    }

    private fun saveHotel(hotelInfoModel: HotelInfoModel) {
        _savedHotels.update { listHotel ->
            val newHotels = LinkedHashMap<Long, HotelInfoModel>(listHotel)
            newHotels[hotelInfoModel.hotelId] = hotelInfoModel
            newHotels
        }
    }

    @TestOnly
    fun getActivityInfoForTesting(activityId: Long): TripActivityInfo? {
        return _savedActivities.value[activityId]?.toTripActivityInfo()
    }

    @TestOnly
    fun upsertActivityInfo(tripId: Long, vararg activities: TripActivityInfo) {
        _savedActivities.update { oldActivities ->
            val newActivities = LinkedHashMap<Long, TripActivityInfoModel>(oldActivities)
            for (activity in activities) {
                newActivities[activity.activityId] = activity.toTripActivityModel(tripId)
            }
            newActivities
        }
    }

    private fun saveActivityInfo(tripActivityInfoModel: TripActivityInfoModel) {
        _savedActivities.update { oldActivities ->
            val newActivities = LinkedHashMap<Long, TripActivityInfoModel>(oldActivities)
            newActivities[tripActivityInfoModel.activityId] = tripActivityInfoModel
            newActivities
        }
    }

    private fun saveFlightInfo(flightInfoModel: FlightInfoModel) {
        _savedFlightInfos.update { listFlightInfo ->
            val newFlights = LinkedHashMap<Long, FlightInfoModel>(listFlightInfo)
            newFlights[flightInfoModel.flightId] = flightInfoModel
            newFlights
        }
    }

    private fun saveAirportInfo(airportInfo: AirportInfo) {
        _savedAirportInfos.update { listAirportInfo ->
            val newAirports = LinkedHashMap<String, AirportInfo>(listAirportInfo)
            newAirports[airportInfo.code] = airportInfo
            newAirports
        }
    }
}