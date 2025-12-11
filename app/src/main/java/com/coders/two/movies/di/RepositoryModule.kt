package com.coders.two.movies.di

import com.coders.two.movies.data.remote.MovieApi
import com.coders.two.movies.data.repository.MovieRepository
import com.coders.two.movies.data.repository.MovieRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideMovieRepository(api: MovieApi): MovieRepository =
        MovieRepositoryImpl(api)
}
