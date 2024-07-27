package com.minhhnn18898.signin.usecase

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.signin.data.AccountService
import javax.inject.Inject

class CheckValidSignedInUserUseCase @Inject constructor(private val repository: AccountService): UseCase<Unit, Boolean>() {
    override fun run(params: Unit): Boolean {
        return repository.isValidLoggedIn()
    }
}