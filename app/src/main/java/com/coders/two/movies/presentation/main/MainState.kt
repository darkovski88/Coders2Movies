package com.coders.two.movies.presentation.main

import com.coders.two.movies.data.model.MovieDto

internal data class MainState(
    val isLoading: Boolean = false,
    val movies: List<MovieDto> = emptyList(),
    val currentPage: Int = 1,
    val endReached: Boolean = false,
    val error: String? = null
)