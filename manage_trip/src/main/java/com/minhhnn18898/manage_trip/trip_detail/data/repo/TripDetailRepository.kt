package com.minhhnn18898.manage_trip.trip_detail.data.repo

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.manage_trip.trip_detail.data.dao.ActivityInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.AirportInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.FlightInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.dao.HotelInfoDao
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.AirportInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.FlightWithAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.HotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.TripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toAirportInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toFlightInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toFlightInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.toHotelInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toHotelInfoModel
import com.minhhnn18898.manage_trip.trip_detail.data.model.toTripActivityInfo
import com.minhhnn18898.manage_trip.trip_detail.data.model.toTripActivityModel
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailDateTimeFormatter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripDetailRepository @Inject constructor(
    @IODispatcher
    private val ioDispatcher: CoroutineDispatcher,
    private val airportInfoDao: AirportInfoDao,
    private val flightInfoDao: FlightInfoDao,
    private val hotelInfoDao: HotelInfoDao,
    private val activityInfoDao: ActivityInfoDao,
    private val dateTimeFormatter: TripDetailDateTimeFormatter
) {

    suspend fun insertFlightInfo(
        tripId: Long,
        flightInfo: FlightInfo,
        departAirport: AirportInfoModel,
        destinationAirport: AirportInfoModel
    ) = withContext(ioDispatcher) {

        val flightInfoModel = flightInfo.toFlightInfoModel(
            tripId = tripId,
            departAirportCode = departAirport.code,
            destinationAirportCode = destinationAirport.code
        ).copy(
            flightId = 0L
        )

        val resultCodeInsertDepartAirport = airportInfoDao.insert(departAirport)
        val resultCodeInsertDestinationAirport = airportInfoDao.insert(destinationAirport)
        val resultCode = flightInfoDao.insert(flightInfoModel)

        if(resultCodeInsertDepartAirport == -1L && resultCodeInsertDestinationAirport == -1L && resultCode == -1L) {
            throw ExceptionInsertFlightInfo()
        }
    }

    suspend fun updateFlightInfo(
        tripId: Long,
        flightInfo: FlightInfo,
        departAirport: AirportInfoModel,
        destinationAirport: AirportInfoModel
    ) = withContext(ioDispatcher) {

        val flightInfoModel = flightInfo.toFlightInfoModel(
            tripId = tripId,
            departAirportCode = departAirport.code,
            destinationAirportCode = destinationAirport.code
        )

        val resultCodeInsertDepartAirport = airportInfoDao.insert(departAirport)
        val resultCodeInsertDestinationAirport = airportInfoDao.insert(destinationAirport)
        val resultCode = flightInfoDao.update(flightInfoModel)

        if(resultCodeInsertDepartAirport <= 0 || resultCodeInsertDestinationAirport <= 0 ||  resultCode <= 0) {
            throw ExceptionUpdateFlightInfo()
        }
    }

    suspend fun deleteFlightInfo(flightId: Long) = withContext(ioDispatcher) {
        val result = flightInfoDao.delete(flightId)

        if(result <= 0) {
            throw ExceptionDeleteFlightInfo()
        }
    }

    fun getListFlightInfo(tripId: Long): Flow<List<FlightWithAirportInfo>> =
        flightInfoDao
            .getFlights(tripId)
            .map { flightInfoModelList ->
                flightInfoModelList.map {
                    FlightWithAirportInfo(
                        flightInfo = it.toFlightInfo(),
                        departAirport = airportInfoDao.get(it.departAirportCode).first()?.toAirportInfo() ?: AirportInfo(code = it.departAirportCode),
                        destinationAirport = airportInfoDao.get(it.destinationAirportCode).first()?.toAirportInfo() ?: AirportInfo(code = it.destinationAirportCode)
                    )
                }
            }

    suspend fun getFlightInfo(flightId: Long): Flow<FlightWithAirportInfo?> =
        flightInfoDao.getFlight(flightId)
            .map { flightInfo ->
                if (flightInfo != null) {
                    FlightWithAirportInfo(
                        flightInfo = flightInfo.toFlightInfo(),
                        departAirport = airportInfoDao.get(flightInfo.departAirportCode).first()?.toAirportInfo() ?: AirportInfo(code = flightInfo.departAirportCode),
                        destinationAirport = airportInfoDao.get(flightInfo.destinationAirportCode).first()?.toAirportInfo() ?: AirportInfo(code = flightInfo.destinationAirportCode)
                    )
                }
                else {
                    null
                }
            }

    suspend fun insertHotelInfo(tripId: Long, hotelInfo: HotelInfo) = withContext(ioDispatcher) {
        val hotelInfoModel = hotelInfo
            .toHotelInfoModel(tripId)
            .copy(hotelId = 0L)

        val resultCode = hotelInfoDao.insert(hotelInfoModel)
        if(resultCode == -1L) {
            throw ExceptionInsertHotelInfo()
        }
    }

    suspend fun updateHotelInfo(tripId: Long, hotelInfo: HotelInfo) = withContext(ioDispatcher) {
        val hotelInfoModel = hotelInfo.toHotelInfoModel(tripId)
        val result = hotelInfoDao.update(hotelInfoModel)
        if(result <= 0) {
            throw ExceptionUpdateHotelInfo()
        }
    }

    fun getAllHotelInfo(tripId: Long): Flow<List<HotelInfo>> =
        hotelInfoDao
            .getHotels(tripId)
            .map {
                it.toHotelInfo()
            }

    suspend fun getHotelInfo(hotelId: Long): Flow<HotelInfo?> = withContext(ioDispatcher) {
        hotelInfoDao
            .getHotel(hotelId)
            .map {
                it?.toHotelInfo()
            }
    }

    suspend fun deleteHotelInfo(hotelId: Long) = withContext(ioDispatcher) {
        val result = hotelInfoDao.delete(hotelId)

        if(result <= 0) {
            throw ExceptionDeleteHotelInfo()
        }
    }

    suspend fun insertActivityInfo(tripId: Long, activityInfo: TripActivityInfo) = withContext(ioDispatcher) {
        val tripActivityInfoModel = activityInfo
            .toTripActivityModel(tripId)
            .copy(activityId = 0L)

        val resultCode = activityInfoDao.insert(tripActivityInfoModel)
        if(resultCode == -1L) {
            throw ExceptionInsertTripActivityInfo()
        }
    }

    suspend fun updateActivityInfo(tripId: Long, activityInfo: TripActivityInfo) = withContext(ioDispatcher) {
        val tripActivityInfoModel = activityInfo.toTripActivityModel(tripId)
        val result = activityInfoDao.update(tripActivityInfoModel)
        if(result <= 0L) {
            throw ExceptionUpdateTripActivityInfo()
        }
    }

    suspend fun deleteActivityInfo(activityId: Long) = withContext(ioDispatcher) {
        val result = activityInfoDao.delete(activityId)

        if(result <= 0) {
            throw ExceptionDeleteTripActivityInfo()
        }
    }

    private fun getAllActivityInfo(tripId: Long): Flow<List<TripActivityInfo>> =
        activityInfoDao
            .getTripActivities(tripId)
            .map {
                it.toTripActivityInfo()
            }

    fun getSortedActivityInfo(tripId: Long): Flow<Map<Long?, List<TripActivityInfo>>> {
        return getAllActivityInfo(tripId)
            .map { listActivity ->
                listActivity.groupBy {
                    if(it.timeFrom != null) dateTimeFormatter.getStartOfTheDay(it.timeFrom) else null
                }
            }
    }

    fun getActivityInfo(activityId: Long): Flow<TripActivityInfo?> =
        activityInfoDao
            .getTripActivity(activityId).map {
                it?.toTripActivityInfo()
            }
}