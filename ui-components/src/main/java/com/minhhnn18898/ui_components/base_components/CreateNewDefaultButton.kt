package com.minhhnn18898.ui_components.base_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.minhhnn18898.ui_components.theme.typography

@Composable
fun CreateNewDefaultButton(
    text: String,
    modifier: Modifier,
    onClick: () -> Unit
) {

    Row(
        modifier = modifier
            .padding(start = 8.dp, top = 8.dp)
            .clickable {
                onClick.invoke()
            },
        verticalAlignment = Alignment.CenterVertically) {

        Icon(
            modifier = Modifier.size(32.dp),
            imageVector = Icons.Filled.Add,
            contentDescription = "",
            tint = MaterialTheme.colorScheme.tertiary
        )


        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = text,
            style = typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }

}