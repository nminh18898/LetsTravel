package com.minhhnn18898.signin.signin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignInViewModel: ViewModel() {

    private lateinit var auth: FirebaseAuth

    var uiState = mutableStateOf(LoginUiState())
        private set

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick() {

    }

    data class LoginUiState(
        val email: String = "",
        val password: String = ""
    )
}