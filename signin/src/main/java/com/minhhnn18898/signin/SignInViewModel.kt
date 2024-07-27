package com.minhhnn18898.signin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignInViewModel: ViewModel() {

    private lateinit var auth: FirebaseAuth

    var uiState = mutableStateOf(LoginUiState())
        private set

    data class LoginUiState(
        val email: String = "",
        val password: String = ""
    )
}