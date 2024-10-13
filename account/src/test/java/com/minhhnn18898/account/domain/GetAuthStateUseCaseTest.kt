package com.minhhnn18898.account.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.account.test_helper.FakeAccountService
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetAuthStateUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getAuthStateUseCase: GetAuthStateUseCase

    private lateinit var accountService: FakeAccountService

    @Before
    fun setup() {
        accountService = FakeAccountService()
        getAuthStateUseCase = GetAuthStateUseCase(accountService)
    }

    @Test
    fun getAuthState_loggedUser() = runTest {
        // Given
        accountService.setCurrentUser("minhhnn@gmail.com", "123456")

        // When
        val result = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getAuthStateUseCase.execute().toList(result)
        }

        // Then
        Truth.assertThat(result).isEqualTo(mutableListOf(true))
    }

    @Test
    fun getAuthState_notLoggedUser() = runTest {
        // When
        val result = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getAuthStateUseCase.execute().toList(result)
        }

        // Then
        Truth.assertThat(result).isEqualTo(mutableListOf(false))
    }

    @Test
    fun getAuthState_changeStateLoggedIn() = runTest {
        // Given
        val result = mutableListOf<Boolean>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getAuthStateUseCase.execute().toList(result)
        }

        // Initial state, user not logged in
        Truth.assertThat(result[0]).isFalse()

        // Login user
        accountService.setCurrentUser("minhhnn@gmail.com", "123456")
        Truth.assertThat(result[1]).isTrue()

        // Logout user
        accountService.clearAccount()
        Truth.assertThat(result[2]).isFalse()
    }
}