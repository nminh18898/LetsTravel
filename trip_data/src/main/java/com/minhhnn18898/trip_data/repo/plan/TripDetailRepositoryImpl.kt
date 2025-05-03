package com.minhhnn18898.trip_data.repo.plan

import com.minhhnn18898.core.di.IODispatcher
import com.minhhnn18898.trip_data.dao.plan.ActivityInfoDao
import com.minhhnn18898.trip_data.dao.plan.AirportInfoDao
import com.minhhnn18898.trip_data.dao.plan.FlightInfoDao
import com.minhhnn18898.trip_data.dao.plan.HotelInfoDao
import com.minhhnn18898.trip_data.model.plan.AirportInfo
import com.minhhnn18898.trip_data.model.plan.FlightInfo
import com.minhhnn18898.trip_data.model.plan.FlightWithAirportInfo
import com.minhhnn18898.trip_data.model.plan.HotelInfo
import com.minhhnn18898.trip_data.model.plan.TripActivityInfo
import com.minhhnn18898.trip_data.model.plan.toAirportInfo
import com.minhhnn18898.trip_data.model.plan.toAirportInfoModel
import com.minhhnn18898.trip_data.model.plan.toFlightInfo
import com.minhhnn18898.trip_data.model.plan.toFlightInfoModel
import com.minhhnn18898.trip_data.model.plan.toHotelInfo
import com.minhhnn18898.trip_data.model.plan.toHotelInfoModel
import com.minhhnn18898.trip_data.model.plan.toTripActivityInfo
import com.minhhnn18898.trip_data.model.plan.toTripActivityModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TripDetailRepositoryImpl @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val airportInfoDao: AirportInfoDao,
    private val flightInfoDao: FlightInfoDao,
    private val hotelInfoDao: HotelInfoDao,
    private val activityInfoDao: ActivityInfoDao
) : TripDetailRepository {

    override suspend fun insertFlightInfo(
        tripId: Long,
        flightInfo: FlightInfo,
        departAirport: AirportInfo,
        destinationAirport: AirportInfo
    ): Long = withContext(ioDispatcher) {

        val flightInfoModel = flightInfo.toFlightInfoModel(
            tripId = tripId,
            departAirportCode = departAirport.code,
            destinationAirportCode = destinationAirport.code
        ).copy(
            flightId = 0L
        )

        val resultCodeInsertDepartAirport = airportInfoDao.insert(departAirport.toAirportInfoModel())
        val resultCodeInsertDestinationAirport = airportInfoDao.insert(destinationAirport.toAirportInfoModel())
        val resultCode = flightInfoDao.insert(flightInfoModel)

        if(resultCodeInsertDepartAirport == -1L && resultCodeInsertDestinationAirport == -1L && resultCode == -1L) {
            throw ExceptionInsertFlightInfo()
        }

        resultCode
    }

    override suspend fun updateFlightInfo(
        tripId: Long,
        flightInfo: FlightInfo,
        departAirport: AirportInfo,
        destinationAirport: AirportInfo
    ) = withContext(ioDispatcher) {

        val flightInfoModel = flightInfo.toFlightInfoModel(
            tripId = tripId,
            departAirportCode = departAirport.code,
            destinationAirportCode = destinationAirport.code
        )

        val resultCodeInsertDepartAirport = airportInfoDao.insert(departAirport.toAirportInfoModel())
        val resultCodeInsertDestinationAirport = airportInfoDao.insert(destinationAirport.toAirportInfoModel())
        val resultCode = flightInfoDao.update(flightInfoModel)

        if(resultCodeInsertDepartAirport <= 0 || resultCodeInsertDestinationAirport <= 0 ||  resultCode <= 0) {
            throw ExceptionUpdateFlightInfo()
        }
    }

    override suspend fun deleteFlightInfo(flightId: Long) = withContext(ioDispatcher) {
        val result = flightInfoDao.delete(flightId)

        if(result <= 0) {
            throw ExceptionDeleteFlightInfo()
        }
    }

    override fun getListFlightInfo(tripId: Long): Flow<List<FlightWithAirportInfo>> =
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

    override fun getFlightInfo(flightId: Long): Flow<FlightWithAirportInfo?> =
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

    override suspend fun insertHotelInfo(tripId: Long, hotelInfo: HotelInfo): Long = withContext(ioDispatcher) {
        val hotelInfoModel = hotelInfo
            .toHotelInfoModel(tripId)
            .copy(hotelId = 0L)

        val resultCode = hotelInfoDao.insert(hotelInfoModel)
        if(resultCode == -1L) {
            throw ExceptionInsertHotelInfo()
        }

        resultCode
    }

    override suspend fun updateHotelInfo(tripId: Long, hotelInfo: HotelInfo) = withContext(ioDispatcher) {
        val hotelInfoModel = hotelInfo.toHotelInfoModel(tripId)
        val result = hotelInfoDao.update(hotelInfoModel)
        if(result <= 0) {
            throw ExceptionUpdateHotelInfo()
        }
    }

    override fun getAllHotelInfo(tripId: Long): Flow<List<HotelInfo>> =
        hotelInfoDao
            .getHotels(tripId)
            .map {
                it.toHotelInfo()
            }

    override fun getHotelInfo(hotelId: Long): Flow<HotelInfo?> =
        hotelInfoDao
            .getHotel(hotelId)
            .map {
                it?.toHotelInfo()
            }

    override suspend fun deleteHotelInfo(hotelId: Long) = withContext(ioDispatcher) {
        val result = hotelInfoDao.delete(hotelId)

        if(result <= 0) {
            throw ExceptionDeleteHotelInfo()
        }
    }

    override suspend fun insertActivityInfo(tripId: Long, activityInfo: TripActivityInfo): Long = withContext(ioDispatcher) {
        val tripActivityInfoModel = activityInfo
            .toTripActivityModel(tripId)
            .copy(activityId = 0L)

        val resultCode = activityInfoDao.insert(tripActivityInfoModel)
        if(resultCode == -1L) {
            throw ExceptionInsertTripActivityInfo()
        }

        resultCode
    }

    override suspend fun updateActivityInfo(tripId: Long, activityInfo: TripActivityInfo) = withContext(ioDispatcher) {
        val tripActivityInfoModel = activityInfo.toTripActivityModel(tripId)
        val result = activityInfoDao.update(tripActivityInfoModel)
        if(result <= 0L) {
            throw ExceptionUpdateTripActivityInfo()
        }
    }

    override suspend fun deleteActivityInfo(activityId: Long) = withContext(ioDispatcher) {
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

    override fun getActivityInfo(activityId: Long): Flow<TripActivityInfo?> =
        activityInfoDao
            .getTripActivity(activityId).map {
                it?.toTripActivityInfo()
            }
}