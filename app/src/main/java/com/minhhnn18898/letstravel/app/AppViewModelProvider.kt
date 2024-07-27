package com.minhhnn18898.letstravel.app

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras

/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {

}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [LetsTravelApplication].
 */
fun CreationExtras.letsTravelApplication(): LetsTravelApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as LetsTravelApplication)