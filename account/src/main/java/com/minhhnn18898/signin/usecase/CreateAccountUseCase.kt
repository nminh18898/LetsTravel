package com.minhhnn18898.signin.usecase

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.signin.data.AccountService
import com.minhhnn18898.signin.data.model.AccountApiResult
import com.minhhnn18898.signin.data.model.UserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class CreateAccountUseCase @Inject constructor(private val accountService: AccountService): UseCase<CreateAccountUseCase.Params, Flow<Result<UserInfo>>>() {
    override fun run(params: Params): Flow<Result<UserInfo>> = callbackFlow {
        trySend(Result.Loading)
        accountService.createUserWithEmailAndPassword(params.email, params.password) {
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