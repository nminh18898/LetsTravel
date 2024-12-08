package com.minhhnn18898.app_navigation.destination

import android.os.Parcelable
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute.Companion.activityIdArg
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute.Companion.flightIdArg
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute.Companion.hotelIdArg
import com.minhhnn18898.app_navigation.destination.route.MainAppRoute.Companion.tripIdArg
import com.minhhnn18898.core.R
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

object EditFlightInfoDestination: AppScreenDestination {
    override val title: Int get() = R.string.flight_info
    override val route: String = MainAppRoute.EDIT_FLIGHT_INFO_SCREEN_ROUTE

    val routeWithArgs = "$route/{$tripIdArg}/{$flightIdArg}"
    val arguments = listOf(
        navArgument(tripIdArg) { type = NavType.LongType },
        navArgument(flightIdArg) { type = NavType.LongType }
    )

    override fun getAllRoutes(): List<String> {
        return mutableListOf(route, routeWithArgs)
    }
}

object EditHotelInfoDestination: AppScreenDestination {
    override val title: Int get() = R.string.hotel_info
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

object EditTripActivityInfoDestination: AppScreenDestination {
    override val title: Int get() = R.string.activity_info
    override val route: String = MainAppRoute.EDIT_TRIP_ACTIVITY_INFO_SCREEN_ROUTE

    val routeWithArgs = "$route/{$tripIdArg}/{$activityIdArg}"
    val arguments = listOf(
        navArgument(tripIdArg) { type = NavType.LongType },
        navArgument(activityIdArg) { type = NavType.LongType }
    )

    override fun getAllRoutes(): List<String> {
        return mutableListOf(route, routeWithArgs)
    }
}

interface TripDetailTabDestination {
    val title: Int
    val icon: Int
}

@Serializable
data class TripDetailDestination(val parameters: TripDetailDestinationParameters)

@Serializable
@Parcelize
data class TripDetailDestinationParameters(
    val tripId: Long
): Parcelable

object TripDetailPlanTabDestination: TripDetailTabDestination {
    override val title: Int
        get() = R.string.plan

    override val icon: Int
        get() = com.minhhnn18898.ui_components.R.drawable.book_5_24
}

object ExpenseTabDestination: TripDetailTabDestination {
    override val title: Int
        get() = R.string.expenses

    override val icon: Int
        get() = com.minhhnn18898.ui_components.R.drawable.attach_money_24
}

object MemoryTabDestination: TripDetailTabDestination {
    override val title: Int
        get() = R.string.memories

    override val icon: Int
        get() = com.minhhnn18898.ui_components.R.drawable.photo_prints_24
}

fun TripDetailTabDestination.isPlanTab(): Boolean {
    return this == TripDetailPlanTabDestination
}

fun TripDetailTabDestination.isExpenseTab(): Boolean {
    return this == ExpenseTabDestination
}

