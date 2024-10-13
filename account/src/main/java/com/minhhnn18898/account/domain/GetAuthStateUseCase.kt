package com.minhhnn18898.account.domain

import com.minhhnn18898.account.data.AccountService
import com.minhhnn18898.architecture.usecase.NoParamUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(private val repository: AccountService): NoParamUseCase<Flow<Boolean>>() {
    override fun run(): Flow<Boolean> {
        return repository.getAuthState()
    }
}