package com.minhhnn18898.ui_components.base_components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun InputTextRow(
    @DrawableRes iconRes: Int,
    label: String,
    inputText: String,
    onTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier,
        verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            leadingIcon = {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(iconRes),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            modifier = modifier.fillMaxWidth(),
            value = inputText,
            onValueChange = onTextChanged,
            label = { Text(label) }
        )
    }
}