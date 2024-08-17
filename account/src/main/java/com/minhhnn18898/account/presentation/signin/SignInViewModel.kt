package com.minhhnn18898.account.presentation.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.core.utils.isValidEmail
import com.minhhnn18898.account.domain.CheckValidSignedInUserUseCase
import com.minhhnn18898.account.domain.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val checkValidSignedInUserUseCase: CheckValidSignedInUserUseCase
): ViewModel() {

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

    fun onSignInClick() {
        viewModelScope.launch {
            signInUseCase.execute(SignInUseCase.Params(uiState.value.email, uiState.value.password))?.collect {
                onShowSaveLoadingState = it is Result.Loading
                when(it) {
                    is Result.Success -> _eventChannel.send(Event.CloseScreen)
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_SIGN_IN)
                    else -> { }
                }
            }
        }
    }

    fun onReceiveLifecycleResume() {
        viewModelScope.launch {
            if(checkValidSignedInUserUseCase.execute(Unit) == true) {
                _eventChannel.send(Event.CloseScreen)
            }
        }
    }

    private fun checkAllowSaveContent() {
        val isValidEmail = uiState.value.email.isValidEmail()
        val isValidPassword = uiState.value.password.isNotBlankOrEmpty()
        allowSaveContent = isValidEmail && isValidPassword
    }

    @Suppress("SameParameterValue")
    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            this@SignInViewModel.errorType = errorType
            delay(3000)
            this@SignInViewModel.errorType = ErrorType.ERROR_MESSAGE_NONE
        }
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_SIGN_IN
    }

    data class LoginUiState(
        val email: String = "",
        val password: String = ""
    )

    sealed class Event {
        data object CloseScreen: Event()
    }
}