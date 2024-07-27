package com.minhhnn18898.signin.signup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.minhhnn18898.signin.R
import com.minhhnn18898.signin.base.EmailField
import com.minhhnn18898.signin.base.PasswordTextField
import com.minhhnn18898.signin.dependency_provider.SignInViewModelProvider

@Composable
fun SignUpScreen(
    modifier: Modifier,
    viewModel: SignUpViewModel = viewModel(factory = SignInViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        EmailField(value = uiState.email, onNewValue = viewModel::onEmailChange)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(value = uiState.password, onNewValue = viewModel::onPasswordChange)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(value = uiState.repeatPassword, onNewValue = viewModel::onRepeatPasswordChange)
        Spacer(modifier = Modifier.height(16.dp))
        SignUpButton(modifier = modifier, onClick = viewModel::onSignUpClick)
    }
}

@Composable
private fun SignUpButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text(
            text = stringResource(id = R.string.sign_up),
        )
    }
}