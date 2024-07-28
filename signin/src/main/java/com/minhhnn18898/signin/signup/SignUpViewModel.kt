package com.minhhnn18898.signin.signup

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.signin.usecase.CreateAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val createAccountUseCase: CreateAccountUseCase): ViewModel() {
    var uiState = mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            createAccountUseCase.execute(CreateAccountUseCase.Params(uiState.value.email, uiState.value.password))?.collect {

            }
        }

    }

    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val repeatPassword: String = ""
    )
}