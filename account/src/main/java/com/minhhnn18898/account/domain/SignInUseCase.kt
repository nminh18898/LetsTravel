package com.minhhnn18898.account.domain

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.account.data.AccountService
import com.minhhnn18898.account.data.model.AccountApiResult
import com.minhhnn18898.account.data.model.UserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SignInUseCase @Inject constructor(private val accountService: AccountService): UseCase<SignInUseCase.Params, Flow<Result<UserInfo>>>() {

    override fun run(params: Params): Flow<Result<UserInfo>> = callbackFlow {
        trySend(Result.Loading)
        accountService.authenticate(params.email, params.password) {
            if(it is AccountApiResult.Success) {
                trySend(Result.Success(it.data))
            } else if(it is AccountApiResult.Error) {
                trySend(Result.Error(it.error))
            }
        }
        awaitClose {
            // do nothing
        }
    }

    data class Params(val email: String, val password: String)
}