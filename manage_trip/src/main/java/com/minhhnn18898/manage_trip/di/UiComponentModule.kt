package com.minhhnn18898.manage_trip.di

import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProvider
import com.minhhnn18898.manage_trip.trip_detail.presentation.expense_tab.manage_member.ManageMemberResourceProviderImpl
import com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab.MemoriesTabResourceProvider
import com.minhhnn18898.manage_trip.trip_detail.presentation.memories_tab.MemoriesTabResourceProviderImpl
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

    @Provides
    fun provideManageMemberResourceProvider(): ManageMemberResourceProvider {
        return ManageMemberResourceProviderImpl()
    }

    @Provides
    fun provideMemoriesTabResourceProvider(): MemoriesTabResourceProvider {
        return MemoriesTabResourceProviderImpl()
    }
}