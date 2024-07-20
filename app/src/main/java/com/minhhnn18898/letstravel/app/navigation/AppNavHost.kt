package com.minhhnn18898.letstravel.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minhhnn18898.letstravel.baseuicomponent.ClearTopBarActions
import com.minhhnn18898.letstravel.homescreen.HomeScreen
import com.minhhnn18898.letstravel.tripinfo.ui.EditTripScreen
import com.minhhnn18898.letstravel.tripinfo.ui.TripInfoListingScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    onScreenDisplay: (AppBarActionsState) -> Unit,
    modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.name,
        modifier = modifier
    ) {
        composable(route = AppScreen.Home.name) {
            HomeScreen(
                onClickEmptyView = {
                    navController.navigate(AppScreen.EditTripInfo.name)
                },
                onClickCreateNew = {
                    navController.navigate(AppScreen.EditTripInfo.name)
                },
                onClickShowAllSavedTrips = {
                    navController.navigate(AppScreen.SavedTripListingFull.name)
                }
            )
            ClearTopBarActions(onScreenDisplay)
        }

        composable(route = AppScreen.EditTripInfo.name) {
            EditTripScreen(
                onComposedTopBarActions = {
                    onScreenDisplay.invoke(it)
                },
                navigateUp = {
                    navController.navigateUp()
                }
            )
        }

        composable(route = AppScreen.SavedTripListingFull.name) {
            TripInfoListingScreen(
                onClickEmptyView = {
                    navController.navigate(AppScreen.EditTripInfo.name)
                },
                onClickCreateNew = {
                    navController.navigate(AppScreen.EditTripInfo.name)
                }
            )
            ClearTopBarActions(onScreenDisplay)
        }
    }
}

