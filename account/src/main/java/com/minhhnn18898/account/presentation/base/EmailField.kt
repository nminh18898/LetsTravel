package com.minhhnn18898.account.presentation.base

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.minhhnn18898.account.R
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun EmailField(
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = { onNewValue(it) },
        label = { Text(stringResource(CommonStringRes.email)) },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.mail_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    )
}