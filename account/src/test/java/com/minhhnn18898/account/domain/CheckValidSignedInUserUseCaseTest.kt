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
class CheckValidSignedInUserUseCaseTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var checkValidSignedInUserUseCase: CheckValidSignedInUserUseCase

    private lateinit var accountService: FakeAccountService

    @Before
    fun setup() {
        accountService = FakeAccountService()
        checkValidSignedInUserUseCase = CheckValidSignedInUserUseCase(accountService)
    }

    @Test
    fun loggedUser() {
        // Given
        accountService.setCurrentUser("minhhnn@gmail.com", "123456")

        // When
        val result = checkValidSignedInUserUseCase.execute()

        // Then
        Truth.assertThat(result).isTrue()
    }

    @Test
    fun notLoggedUser() {
        // When
        val result = checkValidSignedInUserUseCase.execute()

        // Then
        Truth.assertThat(result).isFalse()
    }
}