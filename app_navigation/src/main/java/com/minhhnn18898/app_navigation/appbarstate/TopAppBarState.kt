package com.minhhnn18898.app_navigation.appbarstate

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class TopAppBarState(
    val screenTitle: String = "",
    val actions: (@Composable RowScope.() -> Unit)? = null
)