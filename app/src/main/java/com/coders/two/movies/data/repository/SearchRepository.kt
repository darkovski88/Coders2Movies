package com.coders.two.movies.data.repository

import android.app.appsearch.SearchResult
import com.coders.two.movies.data.model.MovieResponse
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchMovies(query: String, page: Int): MovieResponse
    suspend fun searchSeries(query: String, page: Int): MovieResponse
    suspend fun searchAll(query: String, page: Int): Flow<SearchResult>
}