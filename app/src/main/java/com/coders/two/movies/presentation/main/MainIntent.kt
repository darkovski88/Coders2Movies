package com.coders.two.movies.presentation.main

sealed interface MainIntent {
    data object LoadInitial : MainIntent
    data object LoadNextPage : MainIntent
    data class Search(val query: String) : MainIntent
}