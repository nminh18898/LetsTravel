package com.minhhnn18898.account.test_helper

import com.minhhnn18898.account.data.AccountService
import com.minhhnn18898.account.data.model.AccountApiResult
import com.minhhnn18898.account.data.model.ExceptionCreateAccount
import com.minhhnn18898.account.data.model.ExceptionLogIn
import com.minhhnn18898.account.data.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.TestOnly

class FakeAccountService: AccountService {
    var forceError = false

    private var currentUser: UserInfo? = null

    override fun authenticate(email: String, password: String, onResult: (AccountApiResult) -> Unit) {
        if(forceError) {
            currentUser = null
            onResult(AccountApiResult.Error(ExceptionLogIn()))
        }

        UserInfo(
            email = email,
            displayName = email
        ).let {
            currentUser = it
            onResult(AccountApiResult.Success(it))
        }
    }

    override fun signOut() {
        currentUser = null
    }

    override fun createUserWithEmailAndPassword(email: String, password: String, onResult: (AccountApiResult) -> Unit) {
        if(forceError) {
            onResult(AccountApiResult.Error(ExceptionCreateAccount()))
            return
        }

        val userInfo = UserInfo(
            email = email,
            displayName = email
        )
        currentUser = userInfo
        onResult(AccountApiResult.Success(userInfo))
    }

    override fun isValidLoggedIn(): Boolean {
        return currentUser != null
    }

    override fun getAuthState(): Flow<Boolean> {
        return flow { emit(currentUser != null) }
    }

    override fun getAuthUserInfo(): Flow<UserInfo?> {
        return flow { emit(currentUser) }
    }

    @TestOnly
    fun addAccount(email: String) {
        currentUser = UserInfo(
            email = email,
            displayName = email
        )
    }
}