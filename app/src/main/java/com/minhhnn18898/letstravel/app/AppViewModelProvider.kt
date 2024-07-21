package com.minhhnn18898.letstravel.app

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.minhhnn18898.letstravel.homescreen.HomeScreenViewModel
import com.minhhnn18898.letstravel.tripdetail.ui.edit.EditFlightInfoViewModel
import com.minhhnn18898.letstravel.tripdetail.ui.trip.TripDetailScreenViewModel
import com.minhhnn18898.letstravel.tripdetail.usecase.GetTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.ui.CoverDefaultResourceProvider
import com.minhhnn18898.letstravel.tripinfo.ui.EditTripViewModel
import com.minhhnn18898.letstravel.tripinfo.ui.TripInfoListingViewModel
import com.minhhnn18898.letstravel.tripinfo.usecase.CreateTripInfoUseCase
import com.minhhnn18898.letstravel.tripinfo.usecase.GetListDefaultCoverUseCase
import com.minhhnn18898.letstravel.tripinfo.usecase.GetListTripInfoUseCase

/**
 * Provides Factory to create instance of ViewModel for the entire app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        val defaultCoverResourceProvider = CoverDefaultResourceProvider()

        initializer {
            val getListDefaultCoverUseCase = GetListDefaultCoverUseCase(letsTravelApplication().container.tripInfoRepository)
            val createTripInfoUseCase = CreateTripInfoUseCase(letsTravelApplication().container.tripInfoRepository)
            EditTripViewModel(getListDefaultCoverUseCase, createTripInfoUseCase, defaultCoverResourceProvider)
        }

        initializer {
            EditFlightInfoViewModel()
        }

        initializer {
            val getListTripInfoUseCase = GetListTripInfoUseCase(letsTravelApplication().container.tripInfoRepository)
            HomeScreenViewModel(getListTripInfoUseCase, defaultCoverResourceProvider)
        }

        initializer {
            val getListTripInfoUseCase = GetListTripInfoUseCase(letsTravelApplication().container.tripInfoRepository)
            TripInfoListingViewModel(getListTripInfoUseCase, defaultCoverResourceProvider)
        }

        initializer {
            val saveStateHandle = createSavedStateHandle()
            val getTripInfoUseCase = GetTripInfoUseCase(letsTravelApplication().container.tripInfoRepository)
            TripDetailScreenViewModel(savedStateHandle = saveStateHandle, defaultCoverResourceProvider, getTripInfoUseCase)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [LetsTravelApplication].
 */
fun CreationExtras.letsTravelApplication(): LetsTravelApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as LetsTravelApplication)