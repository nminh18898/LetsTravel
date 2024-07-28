package com.minhhnn18898.signin.data

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.minhhnn18898.signin.data.model.AccountApiResult
import com.minhhnn18898.signin.data.model.ExceptionCreateAccount
import com.minhhnn18898.signin.data.model.ExceptionLogIn
import com.minhhnn18898.signin.data.model.UserInfo
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
}

private fun FirebaseUser.toUserInfo(): UserInfo {
    return UserInfo(this.email ?: "", this.displayName ?: "")
}