package com.coders.two.movies.presentation.main

internal sealed class MainIntent {
    object LoadNextPage : MainIntent()
    class Search(val query: String) : MainIntent()
}