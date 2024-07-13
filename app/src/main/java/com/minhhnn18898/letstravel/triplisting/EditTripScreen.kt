package com.minhhnn18898.letstravel.triplisting

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.minhhnn18898.letstravel.ui.theme.typography

@Composable
fun EditTripScreen(modifier: Modifier = Modifier) {
    Text(
        modifier = Modifier.padding(start = 8.dp),
        text = "Edit trip info",
        style = typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        maxLines = 1
    )
}