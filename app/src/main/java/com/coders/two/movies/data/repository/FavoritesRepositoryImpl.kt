package com.coders.two.movies.data.repository

import com.coders.two.movies.data.db.FavoriteMovieEntity
import com.coders.two.movies.data.db.FavoritesDao
import com.coders.two.movies.data.model.MovieDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val dao: FavoritesDao
) : FavoritesRepository {

    override fun getFavorites(): Flow<List<FavoriteMovieEntity>> = dao.getFavorites()

    override suspend fun toggleFavorite(movie: MovieDto) {
        val entity = FavoriteMovieEntity(
            id = movie.id,
            title = movie.displayTitle,
            posterPath = movie.posterPath
        )

        val isFav = dao.isFavorite(movie.id).first()

        if (isFav) dao.removeFavorite(entity)
        else dao.addFavorite(entity)
    }

    override fun isFavorite(id: Int): Flow<Boolean> = dao.isFavorite(id)
}