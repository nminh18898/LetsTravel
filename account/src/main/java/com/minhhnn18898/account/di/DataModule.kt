package com.minhhnn18898.account.di

import com.minhhnn18898.account.data.AccountService
import com.minhhnn18898.account.data.AccountServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindAccountService(accountServiceRepositoryImpl: AccountServiceImpl): AccountService

}