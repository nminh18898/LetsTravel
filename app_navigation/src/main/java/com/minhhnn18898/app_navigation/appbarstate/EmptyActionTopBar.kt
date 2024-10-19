package com.minhhnn18898.app_navigation.appbarstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun EmptyActionTopBar(screenTitle: String, onScreenDisplay: (TopAppBarState) -> Unit) = LaunchedEffect(key1 = true) {
    onScreenDisplay(TopAppBarState(screenTitle = screenTitle))
}