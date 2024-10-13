package com.minhhnn18898.account.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minhhnn18898.account.domain.CreateAccountUseCase
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
class SignUpViewModel @Inject constructor(private val createAccountUseCase: CreateAccountUseCase): ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEmailChange(newValue: String) {
        _uiState.update {
            it.copy(
                accountRegistrationUiState = it.accountRegistrationUiState.copy(email = newValue)
            )
        }
        checkAllowSaveContent()
    }

    fun onPasswordChange(newValue: String) {
        _uiState.update {
            it.copy(
                accountRegistrationUiState = it.accountRegistrationUiState.copy(password = newValue)
            )
        }
        checkAllowSaveContent()
    }

    fun onRepeatPasswordChange(newValue: String) {
        _uiState.update {
            it.copy(
                accountRegistrationUiState = it.accountRegistrationUiState.copy(repeatPassword = newValue)
            )
        }
        checkAllowSaveContent()
    }

    private fun checkAllowSaveContent() {
        _uiState.update {
            it.copy(allowSaveContent = isAllowSave())
        }
    }

    private fun isAllowSave(): Boolean {
        val inputEmail = uiState.value.accountRegistrationUiState.email
        val inputPassword = uiState.value.accountRegistrationUiState.password
        val repeatPassword = uiState.value.accountRegistrationUiState.repeatPassword

        val isValidEmail = inputEmail.isValidEmail()
        val isValidPassword = inputPassword.isNotBlankOrEmpty() && inputPassword == repeatPassword

        return isValidEmail && isValidPassword
    }

    fun onSignUpClick() {
        viewModelScope.launch {
            createAccountUseCase.execute(CreateAccountUseCase.Params(
                email = uiState.value.accountRegistrationUiState.email,
                password = uiState.value.accountRegistrationUiState.password)
            ).collect { result ->
                when(result) {
                    is Result.Success -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            isAccountCreated = true
                        )
                    }
                    is Result.Error -> showErrorInBriefPeriod(ErrorType.ERROR_MESSAGE_CAN_NOT_CREATE_ACCOUNT)
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
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
        ERROR_MESSAGE_CAN_NOT_CREATE_ACCOUNT
    }

    data class AccountRegistrationUiState(
        val email: String = "",
        val password: String = "",
        val repeatPassword: String = ""
    )

    data class SignUpUiState(
        val accountRegistrationUiState: AccountRegistrationUiState = AccountRegistrationUiState(),
        val isLoading: Boolean = false,
        val showError: ErrorType = ErrorType.ERROR_MESSAGE_NONE,
        val allowSaveContent: Boolean = false,
        val isAccountCreated: Boolean = false
    )
}