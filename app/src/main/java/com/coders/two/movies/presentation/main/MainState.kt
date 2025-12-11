package com.coders.two.movies.presentation.main

import com.coders.two.movies.data.model.MovieDto

internal data class MainState(
    val movies: List<MovieDto> = emptyList(),
    val isLoading: Boolean = false,
    val query: String = "",
    val currentMoviePage: Int = 1,
    val currentTVShowsPage: Int = 1,
    val endReached: Boolean = false,
    val error: String? = null
)