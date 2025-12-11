package com.coders.two.movies.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coders.two.movies.data.repository.PopularMoviesRepository
import com.coders.two.movies.data.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val popularMoviesRepo: PopularMoviesRepository,
    private val searchRepo: SearchRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {

    var state by mutableStateOf(MainState())
        private set
    private val queryFlow = MutableStateFlow("")
    private val _events = MutableSharedFlow<MainUiEvent>()
    val events = _events.asSharedFlow()

    init {
        observeSearch()
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.LoadInitial -> loadInitial()
            MainIntent.LoadNextPage -> loadNextPage()
            is MainIntent.Search -> {
                queryFlow.value = intent.query
            }
        }
    }

    private fun loadInitial() {
        if (state.movies.isNotEmpty()) return

        viewModelScope.launch(dispatcher) {
            loadPopularPage(reset = true)
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearch() {
        viewModelScope.launch(dispatcher) {
            queryFlow
                .debounce(400)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isBlank()) {
                        loadPopularPage(reset = true)
                    } else {
                        searchFirstPage(query)
                    }
                }
        }
    }

    private suspend fun searchFirstPage(query: String) {
        if (state.isLoading) return

        try {
            state = state.copy(
                isLoading = true,
                query = query,
                movies = emptyList(),
                currentMoviePage = 1,
                currentTVShowsPage = 1,
                endReached = false,
                error = null
            )

            val moviesResponse = searchRepo.searchMovies(query = query, page = 1)
            val showsResponse = searchRepo.searchSeries(query = query, page = 1)

            val combined = moviesResponse.results + showsResponse.results

            val endReached =
                (moviesResponse.page >= moviesResponse.totalPages) &&
                        (showsResponse.page >= showsResponse.totalPages)

            state = state.copy(
                isLoading = false,
                movies = combined,
                currentMoviePage = 2,
                currentTVShowsPage = 2,
                endReached = endReached
            )
        } catch (e: Exception) {
            state = state.copy(isLoading = false, error = e.message)
            _events.emit(MainUiEvent.ShowError(e.message ?: "Somwthing went wrong."))
        }
    }

    private fun loadNextPage() {
        if (state.isLoading || state.endReached) return

        viewModelScope.launch(dispatcher) {
            try {
                state = state.copy(isLoading = true, error = null)

                if (state.query.isBlank()) {
                    val popularResult =
                        popularMoviesRepo.getMovies(state.currentMoviePage)

                    val newMovies = state.movies + popularResult.results
                    val endReached =
                        popularResult.page >= popularResult.totalPages

                    state = state.copy(
                        isLoading = false,
                        movies = newMovies,
                        currentMoviePage = state.currentMoviePage + 1,
                        endReached = endReached
                    )
                } else {
                    val movieResult =
                        searchRepo.searchMovies(state.query, state.currentMoviePage)
                    val seriesResult =
                        searchRepo.searchSeries(state.query, state.currentTVShowsPage)

                    val newMovies =
                        state.movies + movieResult.results + seriesResult.results

                    val endReached =
                        (movieResult.page >= movieResult.totalPages) &&
                                (seriesResult.page >= seriesResult.totalPages)

                    state = state.copy(
                        isLoading = false,
                        movies = newMovies,
                        currentMoviePage = state.currentMoviePage + 1,
                        currentTVShowsPage = state.currentTVShowsPage + 1,
                        endReached = endReached
                    )
                }
            } catch (e: Exception) {
                state = state.copy(isLoading = false, error = e.message)
                _events.emit(MainUiEvent.ShowError(e.message ?: "Unknown error"))
            }
        }
    }

    private suspend fun loadPopularPage(reset: Boolean) {
        if (state.isLoading) return

        try {
            state = if (reset) {
                MainState(isLoading = true)
            } else {
                state.copy(isLoading = true, error = null)
            }

            val page = if (reset) 1 else state.currentMoviePage
            val popularResult = popularMoviesRepo.getMovies(page)

            val newMovies =
                if (reset) popularResult.results
                else state.movies + popularResult.results

            val endReached =
                popularResult.page >= popularResult.totalPages

            state = state.copy(
                isLoading = false,
                movies = newMovies,
                currentMoviePage = if (reset) 1 else state.currentMoviePage + 1,
                currentTVShowsPage = 1,
                query = "",
                endReached = endReached
            )
        } catch (e: Exception) {
            state = state.copy(isLoading = false, error = e.message)
            _events.emit(MainUiEvent.ShowError(e.message ?: "Something went wrong."))
        }
    }
}

sealed interface MainUiEvent {
    data class ShowError(val message: String) : MainUiEvent
}
