package com.minhhnn18898.account.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.minhhnn18898.account.presentation.signin.SignInScreen
import com.minhhnn18898.account.presentation.signup.SignUpScreen
import com.minhhnn18898.app_navigation.appbarstate.AppBarActionsState
import com.minhhnn18898.app_navigation.appbarstate.ClearTopBarActions
import com.minhhnn18898.app_navigation.destination.SignUpScreenDestination
import com.minhhnn18898.app_navigation.destination.route.SignInFeatureRoute

fun NavGraphBuilder.signInFeatureComposable(
    navigationController: NavHostController,
    appBarOnScreenDisplay: (AppBarActionsState) -> Unit,
    modifier: Modifier = Modifier,
) {

    composable(route = SignInFeatureRoute.SIGN_IN_SCREEN_ROUTE) {
        SignInScreen(
            modifier = modifier,
            onClickCreateNewAccount = {
                navigationController.navigate(SignUpScreenDestination.route)
            },
            navigateUp = {
                navigationController.navigateUp()
            }
        )
        ClearTopBarActions(appBarOnScreenDisplay)
    }

    composable(route = SignInFeatureRoute.SIGN_UP_SCREEN_ROUTE) {
        SignUpScreen(
            navigateUp = {
                navigationController.navigateUp()
            },
            modifier = modifier
        )
        ClearTopBarActions(appBarOnScreenDisplay)
    }

}