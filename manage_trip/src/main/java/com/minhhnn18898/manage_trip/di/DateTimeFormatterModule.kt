package com.minhhnn18898.manage_trip.di

import com.minhhnn18898.core.utils.BaseDateTimeFormatter
import com.minhhnn18898.core.utils.BaseDateTimeFormatterImpl
import com.minhhnn18898.manage_trip.tripdetail.presentation.trip.TripDetailDateTimeFormatter
import com.minhhnn18898.manage_trip.tripdetail.presentation.trip.TripDetailDateTimeFormatterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object DateTimeFormatterModule {

    @Provides
    fun provideBaseDateTimeFormatter(): BaseDateTimeFormatter {
        return BaseDateTimeFormatterImpl()
    }

    @Provides
    fun provideTripDetailDateTimeFormatter(): TripDetailDateTimeFormatter {
        return TripDetailDateTimeFormatterImpl(BaseDateTimeFormatterImpl())
    }
}