package com.coders.two.movies.data.repository

import com.coders.two.movies.data.model.MovieResponse

interface SearchRepository {
    suspend fun searchMovies(query: String, page: Int): MovieResponse
    suspend fun searchSeries(query: String, page: Int): MovieResponse
}