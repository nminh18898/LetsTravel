package com.minhhnn18898.discover.di

import com.minhhnn18898.discover.data.repo.DiscoverRepository
import com.minhhnn18898.discover.data.repo.DiscoverRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindDiscoverRepository(repositoryImpl: DiscoverRepositoryImpl): DiscoverRepository

}