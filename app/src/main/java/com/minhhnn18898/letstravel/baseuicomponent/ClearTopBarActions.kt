package com.minhhnn18898.letstravel.baseuicomponent

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.minhhnn18898.letstravel.app.navigation.AppBarActionsState


@Composable
fun ClearTopBarActions(onScreenDisplay: (AppBarActionsState) -> Unit) = LaunchedEffect(key1 = true) {
    onScreenDisplay(AppBarActionsState())
}