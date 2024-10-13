package com.minhhnn18898.account.domain

import com.minhhnn18898.account.data.AccountService
import com.minhhnn18898.architecture.usecase.NoParamUseCase
import javax.inject.Inject

class CheckValidSignedInUserUseCase @Inject constructor(private val repository: AccountService): NoParamUseCase<Boolean>() {
    override fun run(): Boolean {
        return repository.isValidLoggedIn()
    }
}