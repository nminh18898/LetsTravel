package com.minhhnn18898.account.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.minhhnn18898.app_navigation.destination.SignUpScreenDestination
import com.minhhnn18898.app_navigation.destination.route.SignInFeatureRoute
import com.minhhnn18898.account.presentation.signin.SignInScreen
import com.minhhnn18898.account.presentation.signup.SignUpScreen

fun NavGraphBuilder.signInFeatureComposable(
    navigationController: NavHostController,
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
    }

    composable(route = SignInFeatureRoute.SIGN_UP_SCREEN_ROUTE) {
        SignUpScreen(
            navigateUp = {
                navigationController.navigateUp()
            },
            modifier = modifier
        )
    }

}