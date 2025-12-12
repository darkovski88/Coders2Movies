package com.coders.two.movies

import com.coders.two.movies.core.ConnectivityProvider
import com.coders.two.movies.data.db.FavoriteMovieEntity
import com.coders.two.movies.data.model.MovieDto
import com.coders.two.movies.data.model.MovieResponse
import com.coders.two.movies.data.repository.FavoritesRepository
import com.coders.two.movies.data.repository.PopularMoviesRepository
import com.coders.two.movies.data.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePopularRepo : PopularMoviesRepository {

    var movies = (1..5).map {
        MovieDto(
            id = it,
            title = "Popular $it",
            name = null,
            overview = "",
            posterPath = null,
            firstAirDate = null,
            releaseDate = "2020-01-0$it",
            voteAverage = 0.0
        )
    }

    override suspend fun getMovies(page: Int): MovieResponse {
        return MovieResponse(
            page = page,
            results = movies,
            totalPages = 3,
            totalResults = 15
        )
    }
}

class FakeSearchRepo : SearchRepository {

    var shouldThrow: Boolean = false

    var movies = (1..3).map {
        MovieDto(
            id = it,
            title = "Movie $it",
            name = null,
            overview = "",
            posterPath = null,
            firstAirDate = null,
            releaseDate = "2020-02-0$it",
            voteAverage = 0.0
        )
    }

    var shows = (1..3).map {
        MovieDto(
            id = 100 + it,
            title = null,
            name = "Show $it",
            overview = "",
            posterPath = null,
            firstAirDate = "2021-03-0$it",
            releaseDate = null,
            voteAverage = 0.0
        )
    }

    override suspend fun searchMovies(query: String, page: Int): MovieResponse {
        if (shouldThrow) {
            throw RuntimeException("Forced error (movies)")
        }

        return MovieResponse(
            page = page,
            results = movies,
            totalPages = 2,
            totalResults = 6
        )
    }

    override suspend fun searchSeries(query: String, page: Int): MovieResponse {
        if (shouldThrow) {
            throw RuntimeException("Forced error (series)")
        }

        return MovieResponse(
            page = page,
            results = shows,
            totalPages = 2,
            totalResults = 6
        )
    }
}

class FakeFavoritesRepo : FavoritesRepository {

    private val favorites = mutableSetOf<Int>()
    private val favoritesFlow =
        MutableStateFlow<List<FavoriteMovieEntity>>(emptyList())

    override fun getFavorites(): Flow<List<FavoriteMovieEntity>> = favoritesFlow

    override fun isFavorite(id: Int): Flow<Boolean> =
        MutableStateFlow(favorites.contains(id))

    override suspend fun toggleFavorite(movie: MovieDto) {
        if (favorites.contains(movie.id)) {
            favorites.remove(movie.id)
        } else {
            favorites.add(movie.id)
        }

        favoritesFlow.value = favorites.map {
            FavoriteMovieEntity(
                id = it, title = movie.displayTitle,
                posterPath = movie.posterPath,
                name = movie.name,
                overview = movie.overview,
                firstAirDate = movie.firstAirDate,
                releaseDate = movie.releaseDate,
                voteAverage = movie.voteAverage
            )
        }
    }
}

class FakeConnectivityProvider(
    initial: Boolean
) : ConnectivityProvider {

    private var connected: Boolean = initial
    private val _connectedFlow = MutableStateFlow(initial)

    override val isConnected: Flow<Boolean> = _connectedFlow

    override fun isCurrentlyConnected(): Boolean = connected

    fun setConnected(value: Boolean) {
        connected = value
        _connectedFlow.value = value
    }
}