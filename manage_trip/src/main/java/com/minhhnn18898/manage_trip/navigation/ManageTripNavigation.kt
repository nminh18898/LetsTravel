package com.minhhnn18898.manage_trip.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.minhhnn18898.app_navigation.appbarstate.AppBarActionsState
import com.minhhnn18898.app_navigation.appbarstate.ClearTopBarActions
import com.minhhnn18898.app_navigation.destination.EditFlightInfoDestination
import com.minhhnn18898.app_navigation.destination.EditHotelInfoDestination
import com.minhhnn18898.app_navigation.destination.EditTripActivityInfoDestination
import com.minhhnn18898.app_navigation.destination.EditTripInfoDestination
import com.minhhnn18898.app_navigation.destination.SavedTripsListingFullDestination
import com.minhhnn18898.app_navigation.destination.TripDetailDestination
import com.minhhnn18898.manage_trip.trip_detail.presentation.activity.AddEditTripActivityScreen
import com.minhhnn18898.manage_trip.trip_detail.presentation.flight.EditFlightInfoScreen
import com.minhhnn18898.manage_trip.trip_detail.presentation.hotel.AddEditHotelInfoScreen
import com.minhhnn18898.manage_trip.trip_detail.presentation.trip.TripDetailScreen
import com.minhhnn18898.manage_trip.trip_info.presentation.edittripinfo.EditTripScreen
import com.minhhnn18898.manage_trip.trip_info.presentation.triplisting.TripInfoListingScreen

fun NavGraphBuilder.manageTripFeatureComposable(
    navController: NavHostController,
    appBarOnScreenDisplay: (AppBarActionsState) -> Unit,
    modifier: Modifier = Modifier,
) {
    composable(
        route = EditTripInfoDestination.routeWithArgs,
        arguments = EditTripInfoDestination.arguments
    ) {
        EditTripScreen(
            onComposedTopBarActions = {
                appBarOnScreenDisplay.invoke(it)
            },
            navigateUp = {
                navController.navigateUp()
            },
            onNavigateToTripDetailScreen = {
                navController.navigateToTripDetailScreen(it)
            }
        )
    }

    composable(route = SavedTripsListingFullDestination.route) {
        TripInfoListingScreen(
            onClickEmptyView = {
                navController.navigateToEditTripScreen()
            },
            onClickCreateNew = {
                navController.navigateToEditTripScreen()
            },
            onClickTripItem = { tripId ->
                navController.navigateToTripDetailScreen(tripId)
            }
        )
        ClearTopBarActions(appBarOnScreenDisplay)
    }

    composable(
        route = TripDetailDestination.routeWithArgs,
        arguments = TripDetailDestination.arguments
    ) {
        TripDetailScreen(
            onComposedTopBarActions = {
                appBarOnScreenDisplay.invoke(it)
            },
            navigateUp = {
                navController.navigateUp()
            },
            onNavigateToEditFlightInfoScreen = { tripId, flightId ->
                navController.navigateToEditFlightScreen(tripId, flightId)
            },
            onNavigateEditHotelScreen = { tripId, hotelId ->
                navController.navigateToEditHotelScreen(tripId, hotelId)
            },
            onNavigateToEditTripScreen = {
                    tripId -> navController.navigateToEditTripScreen(tripId)
            },
            onNavigateEditTripActivityScreen = { tripId, activityId ->
                navController.navigateToEditTripActivityScreen(tripId, activityId)
            }
        )
    }

    composable(
        route = EditFlightInfoDestination.routeWithArgs,
        arguments = EditFlightInfoDestination.arguments
    ) {
        EditFlightInfoScreen(
            onComposedTopBarActions = {
                appBarOnScreenDisplay.invoke(it)
            },
            navigateUp = {
                navController.navigateUp()
            })
    }

    composable(
        route = EditHotelInfoDestination.routeWithArgs,
        arguments = EditHotelInfoDestination.arguments
    ) {
        AddEditHotelInfoScreen(
            onComposedTopBarActions = {
                appBarOnScreenDisplay.invoke(it)
            },
            navigateUp = {
                navController.navigateUp()
            })
    }

    composable(
        route = EditTripActivityInfoDestination.routeWithArgs,
        arguments = EditTripActivityInfoDestination.arguments
    ) {
        AddEditTripActivityScreen(
            onComposedTopBarActions = {
                appBarOnScreenDisplay.invoke(it)
            },
            navigateUp = {
                navController.navigateUp()
            }
        )
    }
}

fun NavHostController.navigateToEditTripScreen() {
    this.navigateToEditTripScreen(tripId = 0L)
}

fun NavHostController.navigateToEditTripScreen(tripId: Long) {
    this.navigate("${EditTripInfoDestination.route}/$tripId")
}

fun NavHostController.navigateToTripDetailScreen(tripId: Long) {
    this.navigate("${TripDetailDestination.route}/$tripId")
}

fun NavHostController.navigateToEditFlightScreen(tripId: Long, flightId: Long) {
    this.navigate("${EditFlightInfoDestination.route}/$tripId/$flightId")
}

fun NavHostController.navigateToEditHotelScreen(tripId: Long, hotelId: Long) {
    this.navigate("${EditHotelInfoDestination.route}/$tripId/$hotelId")
}

fun NavHostController.navigateToEditTripActivityScreen(tripId: Long, activityId: Long = 0L) {
    this.navigate("${EditTripActivityInfoDestination.route}/$tripId/$activityId")
}