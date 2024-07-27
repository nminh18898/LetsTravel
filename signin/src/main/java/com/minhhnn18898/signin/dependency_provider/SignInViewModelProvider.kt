package com.minhhnn18898.signin.dependency_provider

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.minhhnn18898.signin.signin.SignInViewModel
import com.minhhnn18898.signin.signup.SignUpViewModel

object SignInViewModelProvider {

    val Factory = viewModelFactory {
        initializer {
            SignInViewModel()
        }

        initializer {
            SignUpViewModel()
        }
    }

}