package com.minhhnn18898.letstravel.tripinfo.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.minhhnn18898.letstravel.R
import com.minhhnn18898.ui_components.base_components.DefaultEmptyView

@Composable
fun EmptySavedTripView(
    onClick: () -> Unit
) {
    DefaultEmptyView(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        text = stringResource(id = R.string.create_your_first_trip),
        onClick = onClick)
}