package com.minhhnn18898.account.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.minhhnn18898.account.data.model.AccountApiResult
import com.minhhnn18898.account.data.model.ExceptionCreateAccount
import com.minhhnn18898.account.data.model.ExceptionLogIn
import com.minhhnn18898.account.data.model.UserInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(): AccountService {

    companion object {
        private const val TAG = "AccountServiceImpl"
    }

    override fun authenticate(email: String, password: String, onResult: (AccountApiResult) -> Unit) {
        Firebase.auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val currentUser = Firebase.auth.currentUser
                    onResult(AccountApiResult.Success(currentUser?.toUserInfo() ?: UserInfo()))
                } else {
                    Log.w(TAG, "authenticate:failure", task.exception)
                    onResult(AccountApiResult.Error(ExceptionLogIn()))
                }
            }
    }

    override fun createUserWithEmailAndPassword(email: String, password: String, onResult: (AccountApiResult) -> Unit) {
        Firebase.auth
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    Log.i(TAG, "createUserWithEmailAndPassword:success")
                    val currentUser = Firebase.auth.currentUser
                    onResult(AccountApiResult.Success(currentUser?.toUserInfo() ?: UserInfo()))
                } else {
                    Log.w(TAG, "createUserWithEmailAndPassword:failure", task.exception)
                    onResult(AccountApiResult.Error(ExceptionCreateAccount()))
                }
            }
    }

    override fun isValidLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }

    override fun getAuthState(): Flow<Boolean> = callbackFlow {
        val authListener = AuthStateListener {
            val isValid = it.currentUser != null
            trySend(isValid)
        }
        Firebase.auth.addAuthStateListener(authListener)

        awaitClose {
            Firebase.auth.removeAuthStateListener(authListener)
        }
    }

    override fun getAuthUserInfo(): Flow<UserInfo?> = callbackFlow {
        val authListener = AuthStateListener {
            trySend(it.currentUser?.toUserInfo())
        }
        Firebase.auth.addAuthStateListener(authListener)

        awaitClose {
            Firebase.auth.removeAuthStateListener(authListener)
        }
    }

    override fun signOut() {
        Firebase.auth.signOut()
    }
}

private fun FirebaseUser.toUserInfo(): UserInfo {
    return UserInfo(this.email ?: "", this.displayName ?: "")
}