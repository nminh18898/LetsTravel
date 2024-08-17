package com.minhhnn18898.account.usecase

import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.account.data.AccountService
import com.minhhnn18898.account.data.model.UserInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthUserInfoUseCase @Inject constructor(private val repository: AccountService): UseCase<Unit, Flow<UserInfo?>>() {
    override fun run(params: Unit): Flow<UserInfo?> {
        return repository.getAuthUserInfo()
    }
}