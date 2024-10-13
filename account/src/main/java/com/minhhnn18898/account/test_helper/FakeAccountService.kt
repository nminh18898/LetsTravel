package com.minhhnn18898.account.test_helper

import com.minhhnn18898.account.data.AccountService
import com.minhhnn18898.account.data.model.AccountApiResult
import com.minhhnn18898.account.data.model.ExceptionCreateAccount
import com.minhhnn18898.account.data.model.ExceptionLogIn
import com.minhhnn18898.account.data.model.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.jetbrains.annotations.TestOnly

class FakeAccountService: AccountService {
    var forceError = false

    private val savedUser = mutableMapOf<String, String>()

    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    private val observableUser: Flow<UserInfo?> = _currentUser

    override fun authenticate(email: String, password: String, onResult: (AccountApiResult) -> Unit) {
        val isValid = (savedUser[email] == password)

        if(!isValid || forceError) {
            _currentUser.value = null
            onResult(AccountApiResult.Error(ExceptionLogIn()))
            return
        }

        UserInfo(
            email = email,
            displayName = email
        ).let {
            _currentUser.value = it
            onResult(AccountApiResult.Success(it))
        }
    }

    override fun signOut() {
        _currentUser.value = null
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
        _currentUser.value = userInfo
        savedUser[email] = password
        onResult(AccountApiResult.Success(userInfo))
    }

    override fun isValidLoggedIn(): Boolean {
        return _currentUser.value != null
    }

    override fun getAuthState(): Flow<Boolean> {
        return observableUser.map { it != null }
    }

    override fun getAuthUserInfo(): Flow<UserInfo?> {
        return observableUser
    }

    @TestOnly
    fun setCurrentUser(email: String, password: String) {
        savedUser[email] = password

        _currentUser.value = UserInfo(
            email = email,
            displayName = email
        )
    }

    @TestOnly
    fun getCurrentUser(): UserInfo? {
        return _currentUser.value
    }

    @TestOnly
    fun addAccount(email: String, password: String) {
        savedUser[email] = password
    }

    @TestOnly
    fun clearAccount() {
        savedUser.clear()
        _currentUser.value = null
    }
}