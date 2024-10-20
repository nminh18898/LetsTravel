package com.minhhnn18898.photo_view.navigation

import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.minhhnn18898.app_navigation.appbarstate.EmptyActionTopBar
import com.minhhnn18898.app_navigation.appbarstate.TopAppBarState
import com.minhhnn18898.app_navigation.destination.PhotoViewFullDestination
import com.minhhnn18898.app_navigation.destination.PhotoViewFullParam
import com.minhhnn18898.app_navigation.mapper.CustomNavType
import com.minhhnn18898.photo_view.presentation.PhotoViewFullScreen
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.reflect.typeOf

fun NavGraphBuilder.photoViewFullFeatureComposable(
    @Suppress("UNUSED_PARAMETER") navigationController: NavHostController,
    appBarOnScreenDisplay: (TopAppBarState) -> Unit,
    modifier: Modifier = Modifier,
) {

    composable<PhotoViewFullDestination>(
        typeMap = mapOf(typeOf<PhotoViewFullParam>() to CustomNavType(PhotoViewFullParam::class.java, PhotoViewFullParam.serializer()))
    ) {
        val destination = it.toRoute<PhotoViewFullDestination>()
        PhotoViewFullScreen(
            url = destination.param.url,
            modifier = modifier
        )

        EmptyActionTopBar("", appBarOnScreenDisplay)
    }
}


fun NavHostController.navigateToPhotoViewFullScreen(url: String) {
    val encodeUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())

    this.navigate(
        route = PhotoViewFullDestination(
            PhotoViewFullParam(
                url = encodeUrl
            )
        )
    )
}