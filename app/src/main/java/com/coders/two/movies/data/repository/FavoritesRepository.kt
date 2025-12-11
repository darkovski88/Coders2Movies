package com.coders.two.movies.data.repository

import com.coders.two.movies.data.db.FavoriteMovieEntity
import com.coders.two.movies.data.model.MovieDto
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getFavorites(): Flow<List<FavoriteMovieEntity>>
    suspend fun toggleFavorite(movie: MovieDto)
    fun isFavorite(id: Int): Flow<Boolean>
}