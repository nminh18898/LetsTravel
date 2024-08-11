package com.minhhnn18898.signin.data

import com.minhhnn18898.signin.data.model.AccountApiResult
import kotlinx.coroutines.flow.Flow

interface AccountService {
    fun authenticate(email: String, password: String, onResult: (AccountApiResult) -> Unit)
    fun createUserWithEmailAndPassword(email: String, password: String, onResult: (AccountApiResult) -> Unit)
    fun isValidLoggedIn(): Boolean
    fun getAuthState(): Flow<Boolean>
}