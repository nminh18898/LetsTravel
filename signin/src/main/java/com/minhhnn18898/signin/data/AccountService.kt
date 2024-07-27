package com.minhhnn18898.signin.data

interface AccountService {
    fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit)
    fun isValidLoggedIn(): Boolean
}