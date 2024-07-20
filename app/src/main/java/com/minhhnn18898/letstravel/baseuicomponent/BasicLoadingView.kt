package com.minhhnn18898.letstravel.baseuicomponent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class BasicLoadingView {
}

@Composable
fun BasicLoadingView(modifier: Modifier) {
    Box(modifier = modifier
        .padding(vertical = 16.dp)
        .wrapContentHeight()
        .fillMaxWidth(),
        contentAlignment = Alignment.Center) {

        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}