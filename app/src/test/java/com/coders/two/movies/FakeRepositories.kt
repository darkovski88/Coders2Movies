package com.coders.two.movies

import com.coders.two.movies.data.model.MovieDto
import com.coders.two.movies.data.model.MovieResponse
import com.coders.two.movies.data.repository.PopularMoviesRepository
import com.coders.two.movies.data.repository.SearchRepository

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

    // 🔥 THIS is the missing flag you need
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
