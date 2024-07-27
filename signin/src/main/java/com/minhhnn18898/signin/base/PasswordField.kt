package com.minhhnn18898.signin.base

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.minhhnn18898.signin.R
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun PasswordTextField(
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onNewValue(it)
        },
        label = {
            Text(text = stringResource(id = CommonStringRes.password))
        },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val iconRes = if (passwordVisible) R.drawable.visibility_off_24 else R.drawable.visibility_24
            IconButton(onClick = {passwordVisible = !passwordVisible}){
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.lock_24),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier.fillMaxWidth()
    )
}