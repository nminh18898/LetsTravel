package com.minhhnn18898.account.navigation

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.minhhnn18898.account.presentation.signin.SignInScreen
import com.minhhnn18898.account.presentation.signup.SignUpScreen
import com.minhhnn18898.app_navigation.appbarstate.EmptyActionTopBar
import com.minhhnn18898.app_navigation.appbarstate.TopAppBarState
import com.minhhnn18898.app_navigation.destination.SignInScreenDestination
import com.minhhnn18898.app_navigation.destination.SignUpScreenDestination
import com.minhhnn18898.app_navigation.destination.route.SignInFeatureRoute
import com.minhhnn18898.core.utils.StringUtils

fun NavGraphBuilder.signInFeatureComposable(
    navigationController: NavHostController,
    appBarOnScreenDisplay: (TopAppBarState) -> Unit,
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
        EmptyActionTopBar(StringUtils.getString(LocalContext.current, SignInScreenDestination.title), appBarOnScreenDisplay)
    }

    composable(route = SignInFeatureRoute.SIGN_UP_SCREEN_ROUTE) {
        SignUpScreen(
            navigateUp = {
                navigationController.navigateUp()
            },
            modifier = modifier
        )
        EmptyActionTopBar(StringUtils.getString(LocalContext.current, SignUpScreenDestination.title), appBarOnScreenDisplay)
    }

}