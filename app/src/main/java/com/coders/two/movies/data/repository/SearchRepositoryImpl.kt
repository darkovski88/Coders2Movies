package com.coders.two.movies.data.repository

import com.coders.two.movies.data.api.MovieApi
import com.coders.two.movies.data.model.MovieResponse
import javax.inject.Inject

internal class SearchRepositoryImpl @Inject constructor(private val api: MovieApi) :
    SearchRepository {
    override suspend fun searchMovies(query: String, page: Int): MovieResponse =
        api.searchMovies(query = query, page = page)

    override suspend fun searchSeries(
        query: String,
        page: Int
    ): MovieResponse = api.searchTvShows(query = query, page = page)
}

