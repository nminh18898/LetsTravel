package com.minhhnn18898.letstravel.app.appbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.minhhnn18898.account.navigation.signInFeatureComposable
import com.minhhnn18898.app_navigation.appbarstate.EmptyActionTopBar
import com.minhhnn18898.app_navigation.appbarstate.TopAppBarState
import com.minhhnn18898.app_navigation.destination.HomeScreenDestination
import com.minhhnn18898.app_navigation.destination.SavedTripsListingFullDestination
import com.minhhnn18898.app_navigation.destination.SignInScreenDestination
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.discover.navigation.discoverFeatureComposable
import com.minhhnn18898.letstravel.homescreen.HomeScreen
import com.minhhnn18898.manage_trip.navigation.manageTripFeatureComposable
import com.minhhnn18898.manage_trip.navigation.navigateToArticleDetailScreen
import com.minhhnn18898.manage_trip.navigation.navigateToEditTripScreen
import com.minhhnn18898.manage_trip.navigation.navigateToTripDetailScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    appBarOnScreenDisplay: (TopAppBarState) -> Unit,
    modifier: Modifier = Modifier) {

    NavHost(
        navController = navController,
        startDestination = HomeScreenDestination,
        modifier = modifier
    ) {
        composable<HomeScreenDestination> {
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
                onNavigateToArticleDetailScreen = { articleId, articlePosition, listArticles ->
                    navController.navigateToArticleDetailScreen(articleId, articlePosition, listArticles)
                }
            )
            EmptyActionTopBar(StringUtils.getString(LocalContext.current, HomeScreenDestination.title), appBarOnScreenDisplay)
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

