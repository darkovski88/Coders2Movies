package com.coders.two.movies.presentation.main

import com.coders.two.movies.data.model.MovieDto

internal sealed interface MainIntent {
    data object LoadInitial : MainIntent
    data object LoadNextPage : MainIntent
    data class Search(val query: String) : MainIntent
    data class ToggleFavorite(val movie: MovieDto) : MainIntent
}