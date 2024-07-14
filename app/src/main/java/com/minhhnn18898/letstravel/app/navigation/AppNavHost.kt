package com.minhhnn18898.letstravel.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minhhnn18898.letstravel.tripinfo.ui.EditTripScreen
import com.minhhnn18898.letstravel.tripinfo.ui.TripListingScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = AppScreen.Home.name,
        modifier = modifier
    ) {
        composable(route = AppScreen.Home.name) {
            TripListingScreen(onClickEmptyView = {
                navController.navigate(AppScreen.EditTripInfo.name)
            })
        }

        composable(route = AppScreen.EditTripInfo.name) {
            EditTripScreen()
        }
    }
}