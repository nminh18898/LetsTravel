package com.minhhnn18898.account.domain

import com.minhhnn18898.account.data.AccountService
import com.minhhnn18898.architecture.usecase.NoParamUseCase
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val accountService: AccountService): NoParamUseCase<Unit>() {
    override fun run() {
        accountService.signOut()
    }
}