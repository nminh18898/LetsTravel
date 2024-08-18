package com.minhhnn18898.letstravel.app.appbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minhhnn18898.account.navigation.signInFeatureComposable
import com.minhhnn18898.app_navigation.appbarstate.AppBarActionsState
import com.minhhnn18898.app_navigation.appbarstate.ClearTopBarActions
import com.minhhnn18898.app_navigation.destination.EditFlightInfoDestination
import com.minhhnn18898.app_navigation.destination.EditHotelInfoDestination
import com.minhhnn18898.app_navigation.destination.EditTripInfoDestination
import com.minhhnn18898.app_navigation.destination.HomeScreenDestination
import com.minhhnn18898.app_navigation.destination.SavedTripsListingFullDestination
import com.minhhnn18898.app_navigation.destination.SignInScreenDestination
import com.minhhnn18898.app_navigation.destination.TripDetailDestination
import com.minhhnn18898.letstravel.homescreen.HomeScreen
import com.minhhnn18898.letstravel.tripdetail.presentation.flight.EditFlightInfoScreen
import com.minhhnn18898.letstravel.tripdetail.presentation.hotel.EditHotelInfoScreen
import com.minhhnn18898.letstravel.tripdetail.presentation.trip.TripDetailScreen
import com.minhhnn18898.letstravel.tripinfo.presentation.edittripinfo.EditTripScreen
import com.minhhnn18898.letstravel.tripinfo.presentation.triplisting.TripInfoListingScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    appBarOnScreenDisplay: (AppBarActionsState) -> Unit,
    modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = HomeScreenDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeScreenDestination.route) {
            HomeScreen(
                onClickEmptyView = {
                    navController.navigateToEditTripScreen()
                },
                onClickCreateNew = {
                    navController.navigateToEditTripScreen()
                },
                onClickShowAllSavedTrips = {
                    navController.navigate(SavedTripsListingFullDestination.route)
                },
                onClickTripItem = { tripId ->
                    navController.navigateToTripDetailScreen(tripId)
                },
                onNavigateToSignInScreen = {
                    navController.navigate(SignInScreenDestination.route)
                }
            )
            ClearTopBarActions(appBarOnScreenDisplay)
        }

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
                    navController.navigate(EditTripInfoDestination.route)
                },
                onClickCreateNew = {
                    navController.navigate(EditTripInfoDestination.route)
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
            EditHotelInfoScreen(
                onComposedTopBarActions = {
                    appBarOnScreenDisplay.invoke(it)
                },
                navigateUp = {
                    navController.navigateUp()
                })
        }

        signInFeatureComposable(
            navigationController = navController,
            appBarOnScreenDisplay = appBarOnScreenDisplay
        )
    }
}

private fun NavHostController.navigateToEditTripScreen() {
    this.navigateToEditTripScreen(tripId = 0L)
}

private fun NavHostController.navigateToEditTripScreen(tripId: Long) {
    this.navigate("${EditTripInfoDestination.route}/$tripId")
}

private fun NavHostController.navigateToTripDetailScreen(tripId: Long) {
    this.navigate("${TripDetailDestination.route}/$tripId")
}

private fun NavHostController.navigateToEditFlightScreen(tripId: Long, flightId: Long) {
    this.navigate("${EditFlightInfoDestination.route}/$tripId/$flightId")
}

private fun NavHostController.navigateToEditHotelScreen(tripId: Long, hotelId: Long) {
    this.navigate("${EditHotelInfoDestination.route}/$tripId/$hotelId")
}

