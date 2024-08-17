package com.minhhnn18898.account.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.core.utils.isValidEmail
import com.minhhnn18898.account.usecase.CreateAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val createAccountUseCase: CreateAccountUseCase): ViewModel() {
    var uiState = mutableStateOf(LoginUiState())
        private set

    var errorType by mutableStateOf(ErrorType.ERROR_MESSAGE_NONE)
        private set

    var onShowSaveLoadingState by mutableStateOf(false)
        private set

    private val _eventChannel = Channel<Event>()
    val eventTriggerer = _eventChannel.receiveAsFlow()

    var allowSaveContent by mutableStateOf(false)
        private set

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
        checkAllowSaveContent()
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
        checkAllowSaveContent()
    }

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
        checkAllowSaveContent()
    }

    private fun checkAllowSaveContent() {
        val isValidEmail = uiState.value.email.isValidEmail()
        val isValidPassword = uiState.value.password.isNotBlankOrEmpty() && uiState.value.password == uiState.value.repeatPassword
        allowSaveContent = isValidEmail && isValidPassword
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            createAccountUseCase.execute(CreateAccountUseCase.Params(uiState.value.email, uiState.value.password))?.collect {
                onShowSaveLoadingState = it is Result.Loading
                when(it) {
                    is Result.Success -> _eventChannel.send(Event.CloseScreen)
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_ACCOUNT)
                    else -> { }
                }
            }
        }
    }

    @Suppress("SameParameterValue")
    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            this@SignUpViewModel.errorType = errorType
            delay(3000)
            this@SignUpViewModel.errorType = ErrorType.ERROR_MESSAGE_NONE
        }
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_CREATE_ACCOUNT
    }

    data class LoginUiState(
        val email: String = "",
        val password: String = "",
        val repeatPassword: String = ""
    )

    sealed class Event {
        data object CloseScreen: Event()
    }
}