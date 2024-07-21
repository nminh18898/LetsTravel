package com.minhhnn18898.letstravel.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minhhnn18898.letstravel.baseuicomponent.ClearTopBarActions
import com.minhhnn18898.letstravel.homescreen.HomeScreen
import com.minhhnn18898.letstravel.tripdetail.TripDetailScreen
import com.minhhnn18898.letstravel.tripinfo.ui.EditTripScreen
import com.minhhnn18898.letstravel.tripinfo.ui.TripInfoListingScreen

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
                onClickTripItem = {
                    navController.navigate(TripDetailDestination.route)
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
                onClickTripItem = {
                    navController.navigate(TripDetailDestination.route)
                }
            )
            ClearTopBarActions(onScreenDisplay)
        }

        composable(route = TripDetailDestination.route) {
            TripDetailScreen()
            ClearTopBarActions(onScreenDisplay)
        }
    }
}

