package com.minhhnn18898.account

import android.content.Context
import androidx.startup.Initializer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.minhhnn18898.account.BuildConfig

class AuthInitializer : Initializer<FirebaseAuth> {
    private val ENABLE_EMULATOR_DEBUG = false

    // The host '10.0.2.2' is a special IP address to let the
    // Android emulator connect to 'localhost'.
    private val AUTH_EMULATOR_HOST = "10.0.2.2"
    private val AUTH_EMULATOR_PORT = 9099

    override fun create(context: Context): FirebaseAuth {
        val firebaseAuth = Firebase.auth
        // Use emulators only in debug builds
        if (BuildConfig.DEBUG && ENABLE_EMULATOR_DEBUG) {
            firebaseAuth.useEmulator(AUTH_EMULATOR_HOST, AUTH_EMULATOR_PORT)
        }
        return firebaseAuth
    }

    // No dependencies on other libraries
    override fun dependencies(): MutableList<Class<out Initializer<*>>> = mutableListOf()
}