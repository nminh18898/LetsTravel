package com.minhhnn18898.letstravel.app.appbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minhhnn18898.account.navigation.signInFeatureComposable
import com.minhhnn18898.app_navigation.appbarstate.AppBarActionsState
import com.minhhnn18898.app_navigation.appbarstate.ClearTopBarActions
import com.minhhnn18898.app_navigation.destination.HomeScreenDestination
import com.minhhnn18898.app_navigation.destination.SavedTripsListingFullDestination
import com.minhhnn18898.app_navigation.destination.SignInScreenDestination
import com.minhhnn18898.discover.navigation.discoverFeatureComposable
import com.minhhnn18898.letstravel.homescreen.HomeScreen
import com.minhhnn18898.manage_trip.navigation.manageTripFeatureComposable
import com.minhhnn18898.manage_trip.navigation.navigateToArticleDetailScreen
import com.minhhnn18898.manage_trip.navigation.navigateToEditTripScreen
import com.minhhnn18898.manage_trip.navigation.navigateToTripDetailScreen

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
                },
                onNavigateToArticleDetailScreen = { articleId ->
                    navController.navigateToArticleDetailScreen(articleId)
                }
            )
            ClearTopBarActions(appBarOnScreenDisplay)
        }

        manageTripFeatureComposable(
            navController = navController,
            appBarOnScreenDisplay = appBarOnScreenDisplay,
            modifier = modifier
        )

        signInFeatureComposable(
            navigationController = navController,
            appBarOnScreenDisplay = appBarOnScreenDisplay
        )

        discoverFeatureComposable(
            modifier = modifier,
            navigationController = navController,
            appBarOnScreenDisplay = appBarOnScreenDisplay
        )
    }
}

