package com.minhhnn18898.discover.navigation

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.minhhnn18898.app_navigation.appbarstate.EmptyActionTopBar
import com.minhhnn18898.app_navigation.appbarstate.TopAppBarState
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenDestination
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenParameters
import com.minhhnn18898.app_navigation.destination.DiscoveryArticleDetailScreenParametersType
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.discover.presentation.article_detail.ArticleDetailScreen
import kotlin.reflect.typeOf

fun NavGraphBuilder.discoverFeatureComposable(
    @Suppress("UNUSED_PARAMETER") navigationController: NavHostController,
    appBarOnScreenDisplay: (TopAppBarState) -> Unit,
    modifier: Modifier = Modifier,
) {

    composable<DiscoveryArticleDetailScreenDestination>(
        typeMap = mapOf(typeOf<DiscoveryArticleDetailScreenParameters>() to DiscoveryArticleDetailScreenParametersType)
    ) {
        ArticleDetailScreen(modifier = modifier)
        EmptyActionTopBar(StringUtils.getString(LocalContext.current, DiscoveryArticleDetailScreenDestination.title), appBarOnScreenDisplay)
    }
}