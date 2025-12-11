package com.coders.two.movies.presentation.main

internal sealed class MainEvent {
    data class Error(val message: String) : MainEvent()
}