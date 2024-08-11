package com.minhhnn18898.signin.usecase

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.signin.data.AccountService
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val accountService: AccountService): UseCase<Unit, Unit>() {
    override fun run(params: Unit) {
        accountService.signOut()
    }
}