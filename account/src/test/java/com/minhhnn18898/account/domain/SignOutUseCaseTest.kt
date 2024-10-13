package com.minhhnn18898.account.domain

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.minhhnn18898.account.test_helper.FakeAccountService
import com.minhhnn18898.test_utils.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignOutUseCaseTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var signOutUseCase: SignOutUseCase

    private lateinit var accountService: FakeAccountService

    @Before
    fun setup() {
        accountService = FakeAccountService()
        signOutUseCase = SignOutUseCase(accountService)
    }

    @Test
    fun signOutSuccess() {
        // Given
        accountService.setCurrentUser(
            email = "minhhnn@gmail.com",
            password = "123456"
        )

        // When
        signOutUseCase.execute()

        // Then
        Truth.assertThat(accountService.getCurrentUser()).isNull()
    }
}