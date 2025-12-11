package com.coders.two.movies.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coders.two.movies.data.repository.PopularMoviesRepository
import com.coders.two.movies.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val popularMoviesRepo: PopularMoviesRepository,
    private val searchRepo: SearchRepository
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set
    private val queryFlow = MutableStateFlow("")

    init {
        observeSearch()
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.LoadNextPage -> loadNextPage()
            is MainIntent.Search -> {
                queryFlow.value = intent.query
            }
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch {
            queryFlow
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    executeSearch(query)
                }
        }
    }

    private fun executeSearch(query: String) {
        if (query.isBlank()) {
            state = state.copy(query = "", movies = emptyList(), currentMoviePage = 1)
            loadNextPage()
            return
        }
        try {
            state = state.copy(isLoading = true, query = query)
            viewModelScope.launch {
                val moviesResponse = searchRepo.searchMovies(query = query, page = 1)
                val showsResponse = searchRepo.searchSeries(query = query, page = 1)
                val movies = moviesResponse.results
                val shows = showsResponse.results

                val endReached = moviesResponse.page >= moviesResponse.totalPages
                val response = shows + movies
                state = state.copy(
                    isLoading = false,
                    movies = response,
                    currentTVShowsPage = state.currentMoviePage + 1,
                    endReached = endReached
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            state = state.copy(isLoading = false, error = e.message)
        }
    }

    private fun loadNextPage() {
        if (state.isLoading || state.endReached) return
        viewModelScope.launch {
            try {
                var endReached: Boolean
                var newMovies = state.movies

                state = state.copy(isLoading = true)
                if (state.query.isNotEmpty()) {

                    val movieResult = searchRepo.searchMovies(state.query, state.currentMoviePage)
                    val seriesResult =
                        searchRepo.searchSeries(state.query, state.currentTVShowsPage)
                    newMovies = newMovies + movieResult.results + seriesResult.results
                    endReached =
                        movieResult.page >= movieResult.totalPages || seriesResult.page >= seriesResult.totalPages
                } else {
                    val popularMoviesResult = popularMoviesRepo.getMovies(state.currentMoviePage)
                    newMovies = newMovies + popularMoviesResult.results
                    endReached = popularMoviesResult.page >= popularMoviesResult.totalPages
                }

                state = state.copy(
                    isLoading = false,
                    movies = newMovies,
                    currentMoviePage = state.currentMoviePage + 1,
                    endReached = endReached
                )

            } catch (e: Exception) {
                e.printStackTrace()
                state = state.copy(isLoading = false, movies = emptyList(), error = e.message)
            }
        }
    }
}
