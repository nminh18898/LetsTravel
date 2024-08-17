package com.minhhnn18898.account.domain

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.account.data.AccountService
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val accountService: AccountService): UseCase<Unit, Unit>() {
    override fun run(params: Unit) {
        accountService.signOut()
    }
}