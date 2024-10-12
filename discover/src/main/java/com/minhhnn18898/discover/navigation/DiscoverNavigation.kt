package com.minhhnn18898.discover.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.minhhnn18898.app_navigation.appbarstate.AppBarActionsState
import com.minhhnn18898.app_navigation.appbarstate.ClearTopBarActions
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenDestination
import com.minhhnn18898.discover.presentation.article_detail.ArticleDetailScreen

fun NavGraphBuilder.discoverFeatureComposable(
    @Suppress("UNUSED_PARAMETER") navigationController: NavHostController,
    appBarOnScreenDisplay: (AppBarActionsState) -> Unit,
    modifier: Modifier = Modifier,
) {

    composable(
        route = DiscoveryArticleDetailScreenDestination.routeWithArgs,
        arguments = DiscoveryArticleDetailScreenDestination.arguments
    ) {
        ArticleDetailScreen(modifier = modifier)
        ClearTopBarActions(appBarOnScreenDisplay)
    }
}