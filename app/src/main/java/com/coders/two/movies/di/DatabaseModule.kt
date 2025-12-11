package com.coders.two.movies.di

import android.content.Context
import androidx.room.Room
import com.coders.two.movies.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "movies_db").build()

    @Provides
    fun provideFavoritesDao(db: AppDatabase) = db.favoritesDao()
}