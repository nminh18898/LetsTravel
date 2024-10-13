package com.minhhnn18898.account.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.account.domain.CheckValidSignedInUserUseCase
import com.minhhnn18898.account.domain.SignInUseCase
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.core.utils.isNotBlankOrEmpty
import com.minhhnn18898.core.utils.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
    private val checkValidSignedInUserUseCase: CheckValidSignedInUserUseCase
): ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun onEmailChange(newValue: String) {
        _uiState.update {
            it.copy(accountUiState = it.accountUiState.copy(email = newValue))
        }
        checkAllowSaveContent()
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update {
            it.copy(accountUiState = it.accountUiState.copy(password = newValue))
        }
        checkAllowSaveContent()
    }

    fun onSignInClick() {
        viewModelScope.launch {
            signInUseCase.execute(
                SignInUseCase.Params(
                    uiState.value.accountUiState.email,
                    uiState.value.accountUiState.password
                )
            ).collect { result ->
                when(result) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isValidLogin = true,
                                isLoading = false
                            )
                        }
                    }

                    is Result.Error -> {
                        showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_SIGN_IN)
                    }

                    is Result.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true)
                        }
                    }
                }
            }
        }
    }

    fun onReceiveLifecycleResume() {
        viewModelScope.launch {
            if(checkValidSignedInUserUseCase.execute()) {
                _uiState.update {
                    it.copy(isValidLogin = true)
                }
            }
        }
    }

    private fun checkAllowSaveContent() {
        _uiState.update {
            it.copy(allowSaveContent = isAllowSave())
        }
    }

    private fun isAllowSave(): Boolean {
        val isValidEmail = uiState.value.accountUiState.email.isValidEmail()
        val isValidPassword = uiState.value.accountUiState.password.isNotBlankOrEmpty()

        return isValidEmail && isValidPassword
    }

    @Suppress("SameParameterValue")
    private fun showErrorInBriefPeriod(errorType: ErrorType) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    showError = errorType
                )
            }
            delay(3000)
            _uiState.update {
                it.copy(
                    isLoading = false,
                    showError = ErrorType.ERROR_MESSAGE_NONE
                )
            }
        }
    }

    enum class ErrorType {
        ERROR_MESSAGE_NONE,
        ERROR_MESSAGE_CAN_NOT_SIGN_IN
    }

    data class AccountUiState(
        val email: String = "",
        val password: String = ""
    )

    data class SignInUiState(
        val accountUiState: AccountUiState = AccountUiState(),
        val isLoading: Boolean = false,
        val showError: ErrorType = ErrorType.ERROR_MESSAGE_NONE,
        val allowSaveContent: Boolean = false,
        val isValidLogin: Boolean = false
    )
}