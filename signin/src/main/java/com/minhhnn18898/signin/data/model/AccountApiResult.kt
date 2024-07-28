package com.minhhnn18898.signin.data.model

sealed interface AccountApiResult {
    data class Success(val data: UserInfo): AccountApiResult
    data class Error(val error: Throwable?): AccountApiResult
    data object Loading: AccountApiResult
}

class ExceptionCreateAccount: Throwable()
class ExceptionLogIn: Throwable()

