package com.minhhnn18898.letstravel.tripdetail.data.repo

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.letstravel.tripdetail.data.dao.AirportInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.dao.FlightInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.dao.HotelInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfoModel
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfoModel
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightWithAirportInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.HotelInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.HotelInfoModel
import com.minhhnn18898.letstravel.tripdetail.data.model.toFlightInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripDetailRepository @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val airportInfoDao: AirportInfoDao,
    private val flightInfoDao: FlightInfoDao,
    private val hotelInfoDao: HotelInfoDao
) {

    suspend fun insertFlightInfo(
        tripId: Long,
        flightInfo: FlightInfo,
        departAirport: AirportInfoModel,
        destinationAirport: AirportInfoModel
    ) = withContext(ioDispatcher) {

        val flightInfoModel = FlightInfoModel(
            0,
            flightInfo.flightNumber,
            flightInfo.operatedAirlines,
            flightInfo.departureTime,
            flightInfo.arrivalTime,
            flightInfo.price,
            tripId,
            departAirport.code,
            destinationAirport.code
        )

        val resultCodeInsertDepartAirport = airportInfoDao.insert(departAirport)
        val resultCodeInsertDestinationAirport = airportInfoDao.insert(destinationAirport)
        val resultCode = flightInfoDao.insert(flightInfoModel)

        if(resultCodeInsertDepartAirport == -1L && resultCodeInsertDestinationAirport == -1L && resultCode == -1L) {
            throw ExceptionInsertFlightInfo()
        }
    }

    fun getFlightInfo(tripId: Long): Flow<List<FlightWithAirportInfo>> =
        flightInfoDao
            .getFlights(tripId)
            .mapWithAirportInfo(airportInfoDao.getAll())

    class ExceptionInsertFlightInfo: Exception()

    fun getHotelInfo(tripId: Long): Flow<List<HotelInfo>> =
        hotelInfoDao
            .getHotels(tripId)
            .map {
                it.toHotelInfo()
            }
}

private fun Map<String, AirportInfoModel>.findAirportInfo(code: String): AirportInfoModel {
    return this[code] ?: AirportInfoModel("", "", "")
}

private fun Flow<List<FlightInfoModel>>.mapWithAirportInfo(airportFlow: Flow<List<AirportInfoModel>>): Flow<List<FlightWithAirportInfo>> =
    combine(airportFlow) { flightInfos, airportInfos ->
        val airportMap = airportInfos.associateBy( { it.code }, { it } )
        flightInfos.map { flightInfoItem ->
            FlightWithAirportInfo(
                flightInfoItem.toFlightInfo(),
                airportMap.findAirportInfo(flightInfoItem.departAirportCode),
                airportMap.findAirportInfo(flightInfoItem.destinationAirportCode)
            )
        }
    }

private fun List<HotelInfoModel>.toHotelInfo(): List<HotelInfo> {
    return this.map { it.toHotelInfo() }
}

private fun HotelInfoModel.toHotelInfo(): HotelInfo {
    return HotelInfo(
        hotelId = hotelId,
        hotelName = hotelName,
        address = address,
        checkInDate = checkInDate,
        checkOutDate = checkOutDate,
        price = price
    )
}