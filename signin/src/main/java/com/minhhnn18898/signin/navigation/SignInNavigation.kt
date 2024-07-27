package com.minhhnn18898.signin.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.minhhnn18898.app_navigation.destination.route.SignInFeatureRoute
import com.minhhnn18898.signin.signup.SignUpScreen

fun NavGraphBuilder.signUpScreenComposable(
    modifier: Modifier = Modifier
) {

    composable(route = SignInFeatureRoute.SIGN_UP_SCREEN_ROUTE) {
        SignUpScreen(modifier = modifier)
    }

}