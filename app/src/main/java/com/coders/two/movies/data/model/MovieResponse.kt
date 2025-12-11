package com.coders.two.movies.data.model

import com.squareup.moshi.Json

data class MovieResponse(
    val page: Int,
    val results: List<MovieDto>,
    @Json(name = "total_pages") val totalPages: Int,
    @Json(name = "total_results") val totalResults: Int
)