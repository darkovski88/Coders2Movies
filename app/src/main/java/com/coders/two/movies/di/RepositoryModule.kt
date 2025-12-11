package com.coders.two.movies.di

import com.coders.two.movies.data.api.MovieApi
import com.coders.two.movies.data.db.FavoritesDao
import com.coders.two.movies.data.repository.FavoritesRepository
import com.coders.two.movies.data.repository.FavoritesRepositoryImpl
import com.coders.two.movies.data.repository.PopularMoviesRepository
import com.coders.two.movies.data.repository.PopularMoviesRepositoryImpl
import com.coders.two.movies.data.repository.SearchRepository
import com.coders.two.movies.data.repository.SearchRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    fun provideMovieRepository(api: MovieApi): PopularMoviesRepository =
        PopularMoviesRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideSearchRepository(api: MovieApi): SearchRepository = SearchRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideFavoriteRepository(dao: FavoritesDao): FavoritesRepository = FavoritesRepositoryImpl(dao)
}
