package com.minhhnn18898.app_navigation.topappbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable

data class AppBarActionsState(
    val actions: (@Composable RowScope.() -> Unit)? = null
)