package com.coders.two.movies.data.repository

import android.app.appsearch.SearchResult
import com.coders.two.movies.data.model.MovieResponse
import com.coders.two.movies.data.remote.MovieApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

internal class SearchRepositoryImpl @Inject constructor(private val api: MovieApi) :
    SearchRepository {
    override suspend fun searchMovies(query: String, page: Int): MovieResponse =
        api.searchMovies(query = query, page = page)

    override suspend fun searchSeries(
        query: String,
        page: Int
    ): MovieResponse = api.searchTvShows(query = query, page = page)


    override suspend fun searchAll(
        query: String,
        page: Int
    ): Flow<SearchResult> {
        return flowOf()
    }
}

