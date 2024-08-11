package com.minhhnn18898.letstravel.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minhhnn18898.app_navigation.destination.EditFlightInfoDestination
import com.minhhnn18898.app_navigation.destination.EditTripInfoDestination
import com.minhhnn18898.app_navigation.destination.HomeScreenDestination
import com.minhhnn18898.app_navigation.destination.SavedTripsListingFullDestination
import com.minhhnn18898.app_navigation.destination.SignInScreenDestination
import com.minhhnn18898.app_navigation.destination.TripDetailDestination
import com.minhhnn18898.app_navigation.topappbar.AppBarActionsState
import com.minhhnn18898.app_navigation.topappbar.ClearTopBarActions
import com.minhhnn18898.letstravel.homescreen.HomeScreen
import com.minhhnn18898.letstravel.tripdetail.ui.flight.EditFlightInfoScreen
import com.minhhnn18898.letstravel.tripdetail.ui.trip.TripDetailScreen
import com.minhhnn18898.letstravel.tripinfo.ui.EditTripScreen
import com.minhhnn18898.letstravel.tripinfo.ui.TripInfoListingScreen
import com.minhhnn18898.signin.navigation.signInFeatureComposable

@Composable
fun AppNavHost(
    navController: NavHostController,
    onScreenDisplay: (AppBarActionsState) -> Unit,
    modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = HomeScreenDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeScreenDestination.route) {
            HomeScreen(
                onClickEmptyView = {
                    navController.navigate(EditTripInfoDestination.route)
                },
                onClickCreateNew = {
                    navController.navigate(EditTripInfoDestination.route)
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
            ClearTopBarActions(onScreenDisplay)
        }

        composable(route = EditTripInfoDestination.route) {
            EditTripScreen(
                onComposedTopBarActions = {
                    onScreenDisplay.invoke(it)
                },
                navigateUp = {
                    navController.navigateUp()
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
            ClearTopBarActions(onScreenDisplay)
        }

        composable(
            route = TripDetailDestination.routeWithArgs,
            arguments = TripDetailDestination.arguments
        ) {
            TripDetailScreen(onNavigateEditFlightScreen = {
                navController.navigateToEditFlightScreen(it)
            })
            ClearTopBarActions(onScreenDisplay)
        }

        composable(
            route = EditFlightInfoDestination.routeWithArgs,
            arguments = EditFlightInfoDestination.arguments
        ) {
            EditFlightInfoScreen(
                onComposedTopBarActions = {
                    onScreenDisplay.invoke(it)
                },
                navigateUp = {
                    navController.navigateUp()
                })
        }

        signInFeatureComposable(navController)
    }
}

private fun NavHostController.navigateToTripDetailScreen(tripId: Long) {
    this.navigate("${TripDetailDestination.route}/$tripId")
}

private fun NavHostController.navigateToEditFlightScreen(tripId: Long) {
    this.navigate("${EditFlightInfoDestination.route}/$tripId")
}

