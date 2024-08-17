package com.minhhnn18898.account.usecase

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.account.data.AccountService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(private val repository: AccountService): UseCase<Unit, Flow<Boolean>>() {
    override fun run(params: Unit): Flow<Boolean> {
        return repository.getAuthState()
    }
}