package com.minhhnn18898.account.presentation.signin

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.repeatOnLifecycle
import com.minhhnn18898.core.utils.StringUtils
import com.minhhnn18898.account.R
import com.minhhnn18898.account.base.EmailField
import com.minhhnn18898.account.base.PasswordTextField
import com.minhhnn18898.ui_components.base_components.CreateNewDefaultButton
import com.minhhnn18898.ui_components.base_components.ProgressDialog
import com.minhhnn18898.ui_components.base_components.TopMessageBar
import com.minhhnn18898.core.R.string as CommonStringRes

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    onClickCreateNewAccount: () -> Unit,
    navigateUp: () -> Unit,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventTriggerer.collect { event ->
                if(event == SignInViewModel.Event.CloseScreen) {
                    navigateUp.invoke()
                }
            }
        }
    }

    LifecycleResumeEffect(Unit) {
        viewModel.onReceiveLifecycleResume()

        onPauseOrDispose {
            // do nothing
        }
    }

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

        SignInButton(
            modifier = modifier,
            onClick = viewModel::onSignInClick,
            enable = viewModel.allowSaveContent,
        )
        Spacer(modifier = Modifier.height(8.dp))
        CreateNewAccountButton(
            modifier = modifier,
            onClick = onClickCreateNewAccount
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
fun SignInButton(
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

private fun getMessageError(context: Context, errorType: SignInViewModel.ErrorType): String {
    if(errorType == SignInViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_SIGN_IN) {
        return StringUtils.getString(context, R.string.error_can_not_sign_in)
    }

    return ""
}

private fun SignInViewModel.ErrorType.isShow(): Boolean {
    return this != SignInViewModel.ErrorType.ERROR_MESSAGE_NONE
}