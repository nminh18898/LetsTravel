package com.minhhnn18898.app_navigation.destination

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute.Companion.tripIdArg
import kotlinx.serialization.Serializable
import com.minhhnn18898.core.R.string as CommonStringRes

@Serializable
data object HomeScreenDestination {
    val title: Int =  CommonStringRes.app_name
}

object EditTripInfoDestination: AppScreenDestination {
    override val title: Int =  CommonStringRes.trip_info
    override val route: String = MainAppRoute.EDIT_TRIP_SCREEN_ROUTE

    val routeWithArgs = "${route}/{$tripIdArg}"
    val arguments = listOf(
        navArgument(tripIdArg) { type = NavType.LongType }
    )

    override fun getAllRoutes(): List<String> {
        return mutableListOf(route, routeWithArgs)
    }
}

object SavedTripsListingFullDestination: AppScreenDestination {
    override val title: Int = CommonStringRes.saved_trips
    override val route: String = MainAppRoute.SAVED_TRIPS_LISTING_SCREEN_ROUTE
}