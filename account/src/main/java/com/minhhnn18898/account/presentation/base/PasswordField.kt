package com.minhhnn18898.account.presentation.base

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import com.minhhnn18898.account.R
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun PasswordTextField(
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int = R.drawable.lock_24,
    @StringRes labelRes: Int = CommonStringRes.password,
) {
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = {
            onNewValue(it)
        },
        label = {
            Text(text = stringResource(id = labelRes))
        },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val visibilityIconRes = if (passwordVisible) R.drawable.visibility_off_24 else R.drawable.visibility_24
            IconButton(onClick = {passwordVisible = !passwordVisible}){
                Icon(
                    painter = painterResource(id = visibilityIconRes),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
fun RepeatPasswordTextField(
    value: String,
    onNewValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    PasswordTextField(
        value = value,
        onNewValue = onNewValue,
        modifier = modifier,
        iconRes = R.drawable.sync_lock_24,
        labelRes = CommonStringRes.repeat_password
    )
}