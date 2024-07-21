package com.minhhnn18898.letstravel.app.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.minhhnn18898.letstravel.R

interface AppScreenDestination {
    @get:StringRes
    val title: Int
    val route: String

    companion object {
        const val HOME_SCREEN_ROUTE = "home_screen_route"
        const val EDIT_TRIP_SCREEN_ROUTE = "edit_trip_screen_route"
        const val SAVED_TRIPS_LISTING_SCREEN_ROUTE = "saved_trips_listing_screen_route"
        const val TRIP_DETAIL_SCREEN_ROUTE = "trip_detail_screen_route"

        fun getAppScreenDestination(route: String): AppScreenDestination {
            return when(route) {
                HOME_SCREEN_ROUTE -> HomeScreenDestination
                EDIT_TRIP_SCREEN_ROUTE -> EditTripInfoDestination
                SAVED_TRIPS_LISTING_SCREEN_ROUTE -> SavedTripsListingFullDestination
                TRIP_DETAIL_SCREEN_ROUTE -> TripDetailDestination
                else -> HomeScreenDestination
            }
        }
    }
}

object HomeScreenDestination: AppScreenDestination {
    override val title: Int =  R.string.app_name
    override val route: String = AppScreenDestination.HOME_SCREEN_ROUTE
}

object EditTripInfoDestination: AppScreenDestination {
    override val title: Int =  R.string.trip_info
    override val route: String = AppScreenDestination.EDIT_TRIP_SCREEN_ROUTE
}

object SavedTripsListingFullDestination: AppScreenDestination {
    override val title: Int = R.string.saved_trips
    override val route: String = AppScreenDestination.SAVED_TRIPS_LISTING_SCREEN_ROUTE
}

object TripDetailDestination: AppScreenDestination {
    override val title: Int = R.string.trip_detail
    override val route: String = AppScreenDestination.TRIP_DETAIL_SCREEN_ROUTE
}

data class AppBarActionsState(
    val actions: (@Composable RowScope.() -> Unit)? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    currentScreen: AppScreenDestination,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    actions: @Composable (RowScope.() -> Unit),
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                color = MaterialTheme.colorScheme.primary,
                text = stringResource(currentScreen.title)
            )
        },
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        actions = actions
    )
}