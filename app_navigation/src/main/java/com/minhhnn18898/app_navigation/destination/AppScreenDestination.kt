package com.minhhnn18898.app_navigation.destination

import androidx.annotation.StringRes

interface AppScreenDestination {
    @get:StringRes
    val title: Int
    val route: String

    fun getAllRoutes(): List<String> {
        return mutableListOf(route)
    }

    companion object {
        fun getAppScreenDestination(route: String): AppScreenDestination {
           return listAllDestinations.firstOrNull {
               destination -> destination.getAllRoutes().any { it == route }
           } ?: HomeScreenDestination
        }
    }
}