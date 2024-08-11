package com.minhhnn18898.signin.signin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.minhhnn18898.signin.base.EmailField
import com.minhhnn18898.signin.base.PasswordTextField
import com.minhhnn18898.ui_components.base_components.CreateNewDefaultButton
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onClickCreateNewAccount: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        EmailField(value = uiState.email, onNewValue = viewModel::onEmailChange)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            value = uiState.password,
            onNewValue = viewModel::onPasswordChange
        )

        Spacer(modifier = Modifier.height(16.dp))

        SignInButton(modifier = modifier, onClick = viewModel::onSignInClick)
        Spacer(modifier = Modifier.height(8.dp))
        CreateNewAccountButton(modifier = modifier, onClick = onClickCreateNewAccount)
    }
}

@Composable
fun SignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = CommonStringRes.sign_in),
        )
    }
}

@Composable
fun CreateNewAccountButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.BottomStart
    ) {
        CreateNewDefaultButton(
            text = stringResource(id = CommonStringRes.create_new_account),
            modifier = Modifier,
            onClick = onClick)
    }
}