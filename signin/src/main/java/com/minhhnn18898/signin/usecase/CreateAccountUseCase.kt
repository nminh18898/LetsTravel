package com.minhhnn18898.signin.usecase

import com.minhhnn18898.architecture.usecase.Result
import com.minhhnn18898.architecture.usecase.UseCase
import com.minhhnn18898.signin.data.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CreateAccountUseCase: UseCase<Unit, Flow<Result<Flow<UserInfo>>>>() {
    override fun run(params: Unit): Flow<Result<Flow<UserInfo>>> = callbackFlow {
        send(Result.Loading)
    }
}