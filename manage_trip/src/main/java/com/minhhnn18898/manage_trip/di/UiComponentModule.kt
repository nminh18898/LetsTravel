package com.minhhnn18898.manage_trip.di

import com.minhhnn18898.manage_trip.trip_info.presentation.base.CoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ICoverDefaultResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.ITripActivityDateSeparatorResourceProvider
import com.minhhnn18898.manage_trip.trip_info.presentation.base.TripActivityDateSeparatorResourceProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object UiComponentModule {

    @Provides
    fun provideCoverDefaultResourceProvider(): ICoverDefaultResourceProvider {
        return CoverDefaultResourceProvider()
    }

    @Provides
    fun provideTripActivityDateSeparatorResourceProvider(): ITripActivityDateSeparatorResourceProvider {
        return TripActivityDateSeparatorResourceProvider()
    }
}