@file:OptIn(ExperimentalCoroutinesApi::class)

package com.minhhnn18898.account.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.account.data.model.UserInfo
import com.minhhnn18898.account.test_helper.FakeAccountService
import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CreateAccountUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var createAccountUseCase: CreateAccountUseCase

    private lateinit var accountService: FakeAccountService

    @Before
    fun setup() {
        accountService = FakeAccountService()
        createAccountUseCase = CreateAccountUseCase(accountService)
    }

    @Test
    fun createValidAccount() = runTest {
        // When
        val result = mutableListOf<Result<UserInfo>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            createAccountUseCase.execute(
                CreateAccountUseCase.Params(
                    email = "minhhnn@gmail.com",
                    password = "123456"
                )
            ).toList(result)
        }

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Success::class.java)
        Truth.assertThat((result[1] as Result.Success<UserInfo>).data).isEqualTo(
            UserInfo(
                email = "minhhnn@gmail.com",
                displayName = "minhhnn@gmail.com"
            )
        )
    }

    @Test
    fun createAccount_hasError() = runTest {
        // Given
        accountService.forceError = true

        // When
        val result = mutableListOf<Result<UserInfo>>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            createAccountUseCase.execute(
                CreateAccountUseCase.Params(
                    email = "minhhnn@gmail.com",
                    password = "123456"
                )
            ).toList(result)
        }

        // Then
        Truth.assertThat(result).hasSize(2)
        Truth.assertThat(result[0]).isInstanceOf(Result.Loading::class.java)
        Truth.assertThat(result[1]).isInstanceOf(Result.Error::class.java)
    }
}