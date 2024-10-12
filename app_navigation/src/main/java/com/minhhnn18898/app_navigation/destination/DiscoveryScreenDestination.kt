package com.minhhnn18898.app_navigation.destination

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.minhhnn18898.app_navigation.destination.route.DiscoveryFeatureRoute
import com.minhhnn18898.app_navigation.destination.route.DiscoveryFeatureRoute.Companion.articleIdArg
import com.minhhnn18898.core.R

object DiscoveryArticleDetailScreenDestination: AppScreenDestination {
    override val title: Int =  R.string.article
    override val route: String = DiscoveryFeatureRoute.ARTICLE_DETAIL_SCREEN_ROUTE

    val routeWithArgs = "${route}/{$articleIdArg}"
    val arguments = listOf(
        navArgument(articleIdArg) { type = NavType.StringType }
    )

    override fun getAllRoutes(): List<String> {
        return mutableListOf(route, routeWithArgs)
    }
}