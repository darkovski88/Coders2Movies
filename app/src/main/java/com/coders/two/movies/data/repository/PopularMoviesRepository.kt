package com.coders.two.movies.data.repository

import com.coders.two.movies.data.model.MovieResponse

interface PopularMoviesRepository {
    suspend fun getMovies(page: Int): MovieResponse
}