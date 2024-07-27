package com.minhhnn18898.app_navigation.topappbar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect


@Composable
fun ClearTopBarActions(onScreenDisplay: (AppBarActionsState) -> Unit) = LaunchedEffect(key1 = true) {
    onScreenDisplay(AppBarActionsState())
}