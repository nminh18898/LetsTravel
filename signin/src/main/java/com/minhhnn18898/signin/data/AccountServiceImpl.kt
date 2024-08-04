package com.minhhnn18898.signin.data

import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.auth
import javax.inject.Inject

class AccountServiceImpl @Inject constructor(): AccountService {
    override fun authenticate(email: String, password: String, onResult: (Throwable?) -> Unit) {
        Firebase.auth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { onResult(it.exception) }
    }

    override fun linkAccount(email: String, password: String, onResult: (Throwable?) -> Unit) {
        val credential = EmailAuthProvider.getCredential(email, password)

        Firebase.auth.currentUser
            ?.linkWithCredential(credential)
            ?.addOnCompleteListener { onResult(it.exception) }
    }

    override fun isValidLoggedIn(): Boolean {
        return Firebase.auth.currentUser != null
    }
}