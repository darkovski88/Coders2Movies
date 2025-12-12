package com.coders.two.movies.di

import com.coders.two.movies.core.ConnectivityProvider
import com.coders.two.movies.core.ConnectivityProviderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ConnectivityModule {

    @Binds
    @Singleton
    abstract fun bindConnectivityProvider(impl: ConnectivityProviderImpl): ConnectivityProvider
}