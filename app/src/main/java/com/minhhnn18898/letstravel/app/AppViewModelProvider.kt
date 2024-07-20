package com.minhhnn18898.letstravel.app

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.minhhnn18898.letstravel.tripdetail.editflightinfo.EditFlightInfoViewModel
import com.minhhnn18898.letstravel.tripinfo.ui.EditTripViewModel
import com.minhhnn18898.letstravel.tripinfo.usecase.CreateTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.usecase.GetListDefaultCoverUseCase

/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val getListDefaultCoverUseCase = GetListDefaultCoverUseCase(letsTravelApplication().container.tripInfoRepository)
            val createTripInfoUseCase = CreateTripInfoUseCase(letsTravelApplication().container.tripInfoRepository)
            EditTripViewModel(getListDefaultCoverUseCase, createTripInfoUseCase)
        }

        initializer {
            EditFlightInfoViewModel()
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [LetsTravelApplication].
 */
fun CreationExtras.letsTravelApplication(): LetsTravelApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as LetsTravelApplication)