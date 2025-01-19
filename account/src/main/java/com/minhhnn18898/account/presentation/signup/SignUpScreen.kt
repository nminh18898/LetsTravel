package com.minhhnn18898.account.presentation.signup

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.minhhnn18898.account.R
import com.minhhnn18898.account.presentation.base.EmailField
import com.minhhnn18898.account.presentation.base.PasswordTextField
import com.minhhnn18898.account.presentation.base.RepeatPasswordTextField
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.ui_components.loading_view.ProgressDialog
import com.minhhnn18898.ui_components.base_components.TopMessageBar
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun SignUpScreen(
    modifier: Modifier,
    navigateUp: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        EmailField(value = uiState.accountRegistrationUiState.email, onNewValue = viewModel::onEmailChange)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            value = uiState.accountRegistrationUiState.password,
            onNewValue = viewModel::onPasswordChange)
        Spacer(modifier = Modifier.height(8.dp))
        RepeatPasswordTextField(
            value = uiState.accountRegistrationUiState.repeatPassword,
            onNewValue = viewModel::onRepeatPasswordChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        SignUpButton(
            modifier = modifier,
            enable = uiState.allowSaveContent,
            onClick = viewModel::onSignUpClick
        )
    }

    LaunchedEffect(uiState.isAccountCreated) {
        if(uiState.isAccountCreated) {
            navigateUp()
        }
    }

    AnimatedVisibility(uiState.isLoading) {
        ProgressDialog()
    }

    TopMessageBar(
        shown = uiState.showError.isShow(),
        text = getMessageError(LocalContext.current, uiState.showError)
    )
}

@Composable
private fun SignUpButton(
    onClick: () -> Unit,
    enable: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth(),
        enabled = enable
    ) {
        Text(
            text = stringResource(id = CommonStringRes.sign_up),
        )
    }
}

private fun getMessageError(context: Context, errorType: SignUpViewModel.ErrorType): String {
    if(errorType == SignUpViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_ACCOUNT) {
        return StringUtils.getString(context, R.string.error_can_not_create_account)
    }

    return ""
}

private fun SignUpViewModel.ErrorType.isShow(): Boolean {
    return this != SignUpViewModel.ErrorType.ERROR_MESSAGE_NONE
}