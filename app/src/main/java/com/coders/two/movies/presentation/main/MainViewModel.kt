package com.coders.two.movies.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coders.two.movies.data.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set

    fun onIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.LoadNextPage -> loadNextPage()
        }
    }

    private fun loadNextPage() {
        if (state.isLoading || state.endReached) return

        viewModelScope.launch {
            try {
                state = state.copy(isLoading = true)

                val response = repo.getMovies(state.currentPage)

                val newMovies = state.movies + response.results

                state = state.copy(
                    isLoading = false,
                    movies = newMovies,
                    currentPage = state.currentPage + 1,
                    endReached = response.page >= response.totalPages
                )

            } catch (e: Exception) {
                e.printStackTrace()
                state = state.copy(isLoading = false, error = e.message)
            }
        }
    }
}
