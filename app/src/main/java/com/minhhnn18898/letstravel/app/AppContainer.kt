package com.minhhnn18898.letstravel.app

import android.content.Context
import com.minhhnn18898.letstravel.app.database.UserTripDatabase
import com.minhhnn18898.letstravel.tripdetail.data.repo.TripDetailRepository
import com.minhhnn18898.letstravel.tripinfo.data.repo.TripInfoRepository
import kotlinx.coroutines.Dispatchers

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val tripInfoRepository: TripInfoRepository
    val tripDetailRepository: TripDetailRepository
}

class AppDataContainer(private val context: Context) : AppContainer {

    override val tripInfoRepository: TripInfoRepository by lazy {
        TripInfoRepository(UserTripDatabase.getDatabase(context).tripInfoDao(), Dispatchers.IO)
    }
    
    override val tripDetailRepository: TripDetailRepository by lazy {
        TripDetailRepository(
            Dispatchers.IO,
            UserTripDatabase.getDatabase(context).airportInfoDao(),
            UserTripDatabase.getDatabase(context).flightInfoDao()
        )
    }
}