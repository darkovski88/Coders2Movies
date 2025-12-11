package com.coders.two.movies.data.repository

import com.coders.two.movies.data.model.MovieResponse
import com.coders.two.movies.data.remote.MovieApi
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApi
) : MovieRepository {

    override suspend fun getMovies(page: Int): MovieResponse {
        return api.getPopularMovies(page = page)
    }
}
