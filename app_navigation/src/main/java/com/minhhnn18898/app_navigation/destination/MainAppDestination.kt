package com.minhhnn18898.app_navigation.destination

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute.Companion.hotelIdArg
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute.Companion.tripIdArg
import com.minhhnn18898.core.R.string as CommonStringRes

val listAllDestinations = mutableListOf(
    HomeScreenDestination,
    EditTripInfoDestination,
    SavedTripsListingFullDestination,
    TripDetailDestination,
    EditFlightInfoDestination,
    EditHotelInfoDestination,

    SignInScreenDestination,
    SignUpScreenDestination
)

object HomeScreenDestination: AppScreenDestination {
    override val title: Int =  CommonStringRes.app_name
    override val route: String = MainAppRoute.HOME_SCREEN_ROUTE
}

object EditTripInfoDestination: AppScreenDestination {
    override val title: Int =  CommonStringRes.trip_info
    override val route: String = MainAppRoute.EDIT_TRIP_SCREEN_ROUTE
}

object SavedTripsListingFullDestination: AppScreenDestination {
    override val title: Int = CommonStringRes.saved_trips
    override val route: String = MainAppRoute.SAVED_TRIPS_LISTING_SCREEN_ROUTE
}

object TripDetailDestination: AppScreenDestination {
    override val title: Int = CommonStringRes.trip_detail
    override val route: String = MainAppRoute.TRIP_DETAIL_SCREEN_ROUTE

    val routeWithArgs = "$route/{$tripIdArg}"
    val arguments = listOf(
        navArgument(tripIdArg) { type = NavType.LongType }
    )

    override fun getAllRoutes(): List<String> {
        return mutableListOf(route, routeWithArgs)
    }
}

object EditFlightInfoDestination: AppScreenDestination {
    override val title: Int get() = CommonStringRes.flight_info
    override val route: String = MainAppRoute.EDIT_FLIGHT_INFO_SCREEN_ROUTE

    val routeWithArgs = "$route/{$tripIdArg}"
    val arguments = listOf(
        navArgument(tripIdArg) { type = NavType.LongType }
    )

    override fun getAllRoutes(): List<String> {
        return mutableListOf(route, routeWithArgs)
    }
}

object EditHotelInfoDestination: AppScreenDestination {
    override val title: Int get() = CommonStringRes.hotel_info
    override val route: String = MainAppRoute.EDIT_HOTEL_INFO_SCREEN_ROUTE

    val routeWithArgs = "$route/{$tripIdArg}/{$hotelIdArg}"
    val arguments = listOf(
        navArgument(tripIdArg) { type = NavType.LongType },
        navArgument(hotelIdArg) { type = NavType.LongType }
    )

    override fun getAllRoutes(): List<String> {
        return mutableListOf(route, routeWithArgs)
    }
}