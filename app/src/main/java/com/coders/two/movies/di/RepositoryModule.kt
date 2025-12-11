package com.coders.two.movies.di

import com.coders.two.movies.data.remote.MovieApi
import com.coders.two.movies.data.repository.PopularMoviesRepository
import com.coders.two.movies.data.repository.PopularMoviesRepositoryImpl
import com.coders.two.movies.data.repository.SearchRepository
import com.coders.two.movies.data.repository.SearchRepositoryImpl
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
    fun provideMovieRepository(api: MovieApi): PopularMoviesRepository =
        PopularMoviesRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideSearchRepository(api: MovieApi): SearchRepository = SearchRepositoryImpl(api)
}
