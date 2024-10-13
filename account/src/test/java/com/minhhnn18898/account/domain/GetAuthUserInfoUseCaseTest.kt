package com.minhhnn18898.account.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.account.data.model.UserInfo
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
class GetAuthUserInfoUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getAuthUserInfoUseCase: GetAuthUserInfoUseCase

    private lateinit var accountService: FakeAccountService

    @Before
    fun setup() {
        accountService = FakeAccountService()
        getAuthUserInfoUseCase = GetAuthUserInfoUseCase(accountService)
    }

    @Test
    fun getAuthUser_loggedUser() = runTest {
        // Given
        accountService.setCurrentUser("minhhnn@gmail.com", "123456")

        // When
        val result = mutableListOf<UserInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getAuthUserInfoUseCase.execute().toList(result)
        }

        // Then
        Truth.assertThat(result).isEqualTo(
            mutableListOf(
                UserInfo(
                    email = "minhhnn@gmail.com",
                    displayName = "minhhnn@gmail.com"
                )
            )
        )
    }

    @Test
    fun getAuthUser_notLoggedUser() = runTest {
        // When
        val result = mutableListOf<UserInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getAuthUserInfoUseCase.execute().toList(result)
        }

        // Then
        Truth.assertThat(result).isEqualTo(mutableListOf(null))
    }

    @Test
    fun getAuthUser_changeStateLoggedIn() = runTest {
        // Given
        val result = mutableListOf<UserInfo?>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            getAuthUserInfoUseCase.execute().toList(result)
        }

        // Initial state, user not logged in
        Truth.assertThat(result[0]).isNull()

        // Login user
        accountService.setCurrentUser("minhhnn@gmail.com", "123456")
        Truth.assertThat(result[1]).isEqualTo(
            UserInfo(
                email = "minhhnn@gmail.com",
                displayName = "minhhnn@gmail.com"
            )
        )

        // Logout user
        accountService.clearAccount()
        Truth.assertThat(result[2]).isNull()
    }
}