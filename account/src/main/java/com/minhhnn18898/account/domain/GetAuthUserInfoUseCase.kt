package com.minhhnn18898.account.domain

import com.minhhnn18898.account.data.AccountService
import com.minhhnn18898.account.data.model.UserInfo
import com.minhhnn18898.architecture.usecase.NoParamUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAuthUserInfoUseCase @Inject constructor(private val repository: AccountService): NoParamUseCase<Flow<UserInfo?>>() {
    override fun run(): Flow<UserInfo?> {
        return repository.getAuthUserInfo()
    }
}