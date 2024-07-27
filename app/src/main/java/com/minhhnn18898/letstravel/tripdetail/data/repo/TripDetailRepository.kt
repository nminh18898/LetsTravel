package com.minhhnn18898.letstravel.tripdetail.data.repo

import com.minhhnn18898.letstravel.app.di.IODispatcher
import com.minhhnn18898.letstravel.tripdetail.data.dao.AirportInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.dao.FlightInfoDao
import com.minhhnn18898.letstravel.tripdetail.data.model.AirportInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightInfoModel
import com.minhhnn18898.letstravel.tripdetail.data.model.FlightWithAirportInfo
import com.minhhnn18898.letstravel.tripdetail.data.model.toFlightInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripDetailRepository @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val airportInfoDao: AirportInfoDao,
    private val flightInfoDao: FlightInfoDao,
) {

    suspend fun insertFlightInfo(
        tripId: Long,
        flightInfo: FlightInfo,
        departAirport: AirportInfo,
        destinationAirport: AirportInfo
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
}

private fun Map<String, AirportInfo>.findAirportInfo(code: String): AirportInfo {
    return this[code] ?: AirportInfo("", "", "")
}

private fun Flow<List<FlightInfoModel>>.mapWithAirportInfo(airportFlow: Flow<List<AirportInfo>>): Flow<List<FlightWithAirportInfo>> =
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