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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.minhhnn18898.account.base.EmailField
import com.minhhnn18898.account.base.PasswordTextField
import com.minhhnn18898.account.base.RepeatPasswordTextField
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.account.R
import com.minhhnn18898.ui_components.base_components.ProgressDialog
import com.minhhnn18898.ui_components.base_components.TopMessageBar
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun SignUpScreen(
    modifier: Modifier,
    navigateUp: () -> Unit,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventTriggerer.collect { event ->
                if(event == SignUpViewModel.Event.CloseScreen) {
                    navigateUp.invoke()
                }
            }
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        EmailField(value = uiState.email, onNewValue = viewModel::onEmailChange)
        Spacer(modifier = Modifier.height(8.dp))
        PasswordTextField(
            value = uiState.password,
            onNewValue = viewModel::onPasswordChange)
        Spacer(modifier = Modifier.height(8.dp))
        RepeatPasswordTextField(
            value = uiState.repeatPassword,
            onNewValue = viewModel::onRepeatPasswordChange
        )
        Spacer(modifier = Modifier.height(16.dp))
        SignUpButton(
            modifier = modifier,
            enable = viewModel.allowSaveContent,
            onClick = viewModel::onSignUpClick
        )
    }

    AnimatedVisibility(viewModel.onShowSaveLoadingState) {
        ProgressDialog()
    }

    TopMessageBar(
        shown = viewModel.errorType.isShow(),
        text = getMessageError(LocalContext.current, viewModel.errorType)
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