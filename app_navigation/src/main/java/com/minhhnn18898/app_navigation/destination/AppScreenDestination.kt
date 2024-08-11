package com.minhhnn18898.app_navigation.destination

import androidx.annotation.StringRes
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.app_navigation.destination.route.SignInFeatureRoute

interface AppScreenDestination {
    @get:StringRes
    val title: Int
    val route: String

    companion object {
        fun getAppScreenDestination(route: String): AppScreenDestination {
            return when(route) {
                MainAppRoute.HOME_SCREEN_ROUTE -> HomeScreenDestination
                MainAppRoute.EDIT_TRIP_SCREEN_ROUTE -> EditTripInfoDestination
                MainAppRoute.SAVED_TRIPS_LISTING_SCREEN_ROUTE -> SavedTripsListingFullDestination
                MainAppRoute.TRIP_DETAIL_SCREEN_ROUTE -> TripDetailDestination
                MainAppRoute.EDIT_FLIGHT_INFO_SCREEN_ROUTE -> EditFlightInfoDestination

                SignInFeatureRoute.SIGN_IN_SCREEN_ROUTE -> SignInScreenDestination
                SignInFeatureRoute.SIGN_UP_SCREEN_ROUTE -> SignUpScreenDestination

                else -> HomeScreenDestination
            }
        }
    }
}