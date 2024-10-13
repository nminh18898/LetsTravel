package com.minhhnn18898.account.presentation.signin

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.account.domain.CheckValidSignedInUserUseCase
import com.minhhnn18898.account.domain.SignInUseCase
import com.minhhnn18898.account.test_helper.FakeAccountService
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var accountService: FakeAccountService
    private lateinit var signInUseCase: SignInUseCase
    private lateinit var checkValidSignedInUserUseCase: CheckValidSignedInUserUseCase

    private lateinit var viewModel: SignInViewModel

    @Before
    fun setup() {
        accountService = FakeAccountService()
        signInUseCase = SignInUseCase(accountService)
        checkValidSignedInUserUseCase = CheckValidSignedInUserUseCase(accountService)
        viewModel = SignInViewModel(
            signInUseCase = signInUseCase,
            checkValidSignedInUserUseCase = checkValidSignedInUserUseCase
        )
    }

    @Test
    fun getUiState_verifyInitialState() {
        val uiState = viewModel.uiState

        Truth.assertThat(uiState.value.accountUiState.email).isEmpty()
        Truth.assertThat(uiState.value.accountUiState.password).isEmpty()
        Truth.assertThat(uiState.value.isLoading).isFalse()
        Truth.assertThat(uiState.value.showError).isEqualTo(SignInViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(uiState.value.allowSaveContent).isFalse()
        Truth.assertThat(uiState.value.isValidLogin).isFalse()
    }

    @Test
    fun onEmailChange_newInfoShown() {
        // When
        viewModel.onEmailChange("minhhnn@gmail.com")

        // Then
        Truth.assertThat(viewModel.uiState.value.accountUiState.email).isEqualTo("minhhnn@gmail.com")
    }

    @Test
    fun onPasswordChange_newInfoShown() {
        // When
        viewModel.onPasswordChange("123456")

        // Then
        Truth.assertThat(viewModel.uiState.value.accountUiState.password).isEqualTo("123456")
    }

    @Test
    fun onCreateAccount_validName_validPassword_verifyAllowSave() {
        // When
        viewModel.onEmailChange("minhhnn@gmail.com")
        viewModel.onPasswordChange("123456")

        // Then
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()
    }

    @Test
    fun onCreateAccount_missingEmail_verifyNotAllowSave() {
        // Given
        viewModel.onEmailChange("minhhnn@gmail.com")
        viewModel.onPasswordChange("123456")

        // Verify initial state: allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // Then - set empty email
        viewModel.onEmailChange("")
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onCreateAccount_missingPassword_verifyNotAllowSave() {
        // Given
        viewModel.onEmailChange("minhhnn@gmail.com")
        viewModel.onPasswordChange("123456")

        // Verify initial state: allow save
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isTrue()

        // Then - set empty password
        viewModel.onPasswordChange("")
        Truth.assertThat(viewModel.uiState.value.allowSaveContent).isFalse()
    }

    @Test
    fun onSignInClick_validUser() {
        // Given
        accountService.addAccount(
            email = "minhhnn@gmail.com",
            password = "123456"
        )

        // When
        viewModel.onEmailChange("minhhnn@gmail.com")
        viewModel.onPasswordChange("123456")
        viewModel.onSignInClick()

        // Then
        val uiState = viewModel.uiState.value
        Truth.assertThat(uiState.isLoading).isFalse()
        Truth.assertThat(uiState.isValidLogin).isTrue()
    }

    @Test
    fun onSignInClick_showLoading() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        accountService.addAccount(
            email = "minhhnn@gmail.com",
            password = "123456"
        )
        viewModel.apply {
            onEmailChange("minhhnn@gmail.com")
            onPasswordChange("123456")
        }

        // When
        val resultList = mutableListOf<SignInViewModel.SignInUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(resultList)
        }

        viewModel.onSignInClick()
        advanceUntilIdle()

        // Then
        Truth.assertThat(resultList).hasSize(3)
        // first item is the state before we invoke onSignInClick(), second and third item is the state after we invoke onSignInClick()
        // We need to assert loading is shown correctly
        Truth.assertThat(resultList[0].isLoading).isFalse()
        Truth.assertThat(resultList[1].isLoading).isTrue()
        Truth.assertThat(resultList[2].isLoading).isFalse()
    }

    @Test
    fun onSignInClick_invalidUser() = runTest {
        Dispatchers.setMain(StandardTestDispatcher())

        // Given
        viewModel.apply {
            onEmailChange("minhhnn@gmail.com")
            onPasswordChange("123456")
        }

        // When
        val resultList = mutableListOf<SignInViewModel.SignInUiState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.toList(resultList)
        }

        viewModel.onSignInClick()
        advanceUntilIdle()

        // Then - assert loading and error is shown correctly
        Truth.assertThat(resultList).hasSize(4)

        // First item is the state before we invoke onSignInClick(),
        Truth.assertThat(resultList[0].isLoading).isFalse()

        // Second item: verify loading is shown right after click sign in
        Truth.assertThat(resultList[1].isLoading).isTrue()

        // Third item: error occurs, verify error state is shown
        Truth.assertThat(resultList[2].showError).isEqualTo(SignInViewModel.ErrorType.ERROR_MESSAGE_CAN_NOT_SIGN_IN)
        Truth.assertThat(resultList[2].isLoading).isFalse()

        // Fourth item: error occurs, verify error state is hidden
        Truth.assertThat(resultList[3].showError).isEqualTo(SignInViewModel.ErrorType.ERROR_MESSAGE_NONE)
        Truth.assertThat(resultList[3].isLoading).isFalse()
    }

    @Test
    fun onReceiveLifecycleResume() {
        // Given
        accountService.setCurrentUser(email = "minhhnn@gmail.com", password = "123456")

        // When
        viewModel.onReceiveLifecycleResume()

        // Then
        Truth.assertThat(viewModel.uiState.value.isValidLogin).isTrue()
    }
}