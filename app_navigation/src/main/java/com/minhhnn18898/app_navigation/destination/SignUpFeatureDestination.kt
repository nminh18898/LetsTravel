package com.minhhnn18898.app_navigation.destination

import com.minhhnn18898.app_navigation.destination.route.SignInFeatureRoute
import com.minhhnn18898.core.R.string as CommonStringRes

object SignInScreenDestination: AppScreenDestination {
    override val title: Int =  CommonStringRes.sign_in
    override val route: String = SignInFeatureRoute.SIGN_IN_SCREEN_ROUTE
}

object SignUpScreenDestination: AppScreenDestination {
    override val title: Int =  CommonStringRes.sign_up
    override val route: String = SignInFeatureRoute.SIGN_UP_SCREEN_ROUTE
}