package com.minhhnn18898.account.data

import com.minhhnn18898.account.data.model.AccountApiResult
import com.minhhnn18898.account.data.model.UserInfo
import kotlinx.coroutines.flow.Flow

interface AccountService {
    fun authenticate(email: String, password: String, onResult: (AccountApiResult) -> Unit)
    fun signOut()
    fun createUserWithEmailAndPassword(email: String, password: String, onResult: (AccountApiResult) -> Unit)
    fun isValidLoggedIn(): Boolean
    fun getAuthState(): Flow<Boolean>
    fun getAuthUserInfo(): Flow<UserInfo?>
}