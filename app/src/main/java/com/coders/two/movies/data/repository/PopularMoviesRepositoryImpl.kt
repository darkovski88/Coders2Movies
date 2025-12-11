package com.coders.two.movies.data.repository

import com.coders.two.movies.data.model.MovieResponse
import com.coders.two.movies.data.remote.MovieApi
import javax.inject.Inject

internal class PopularMoviesRepositoryImpl @Inject constructor(
    private val api: MovieApi
) : PopularMoviesRepository {

    override suspend fun getMovies(page: Int): MovieResponse {
        return api.getPopularMovies(page = page)
    }
}
