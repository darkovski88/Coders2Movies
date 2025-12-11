package com.coders.two.movies.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coders.two.movies.data.model.MovieDto
import com.coders.two.movies.data.repository.FavoritesRepository
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

private const val FIRST_PAGE = 1
private const val SECOND_PAGE = FIRST_PAGE + 1


@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val popularMoviesRepo: PopularMoviesRepository,
    private val searchRepo: SearchRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val favoritesRepo: FavoritesRepository
) : ViewModel() {
    private var favoriteIds: Set<Int> = emptySet()

    var state by mutableStateOf(MainState())
        private set
    private val queryFlow = MutableStateFlow("")
    private val _events = MutableSharedFlow<MainUiEvent>()
    val events = _events.asSharedFlow()

    init {
        observeSearch()
        observeFavorites()
    }

    fun onIntent(intent: MainIntent) {
        when (intent) {
            MainIntent.LoadInitial -> loadInitial()
            MainIntent.LoadNextPage -> loadNextPage()
            is MainIntent.ToggleFavorite -> toggleFavorite(intent.movie)
            is MainIntent.Search -> queryFlow.value = intent.query
        }
    }

    private fun toggleFavorite(movie: MovieDto) {
        viewModelScope.launch(dispatcher) {
            favoritesRepo.toggleFavorite(movie)
        }
    }

    private fun applyFavoriteFlags(list: List<MovieDto>): List<MovieDto> {
        return list.map { movie ->
            movie.copy(isFavorite = favoriteIds.contains(movie.id))
        }
    }

    private fun loadInitial() {
        if (state.movies.isNotEmpty()) return
        viewModelScope.launch(dispatcher) { loadPopularPage(reset = true) }
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
    private fun nextPageAfterFirst() = FIRST_PAGE + 1

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

            val moviesResp = searchRepo.searchMovies(query, 1)
            val showsResp = searchRepo.searchSeries(query, 1)

            val combined = moviesResp.results + showsResp.results
            val marked = applyFavoriteFlags(combined)

            val endReached =
                moviesResp.page >= moviesResp.totalPages &&
                        showsResp.page >= showsResp.totalPages

            state = state.copy(
                isLoading = false,
                movies = marked,
                currentMoviePage = 2,
                currentTVShowsPage = 2,
                endReached = endReached
            )

        } catch (e: Exception) {
            state = state.copy(isLoading = false, error = e.message)
            _events.emit(MainUiEvent.ShowError(e.message ?: "Something went wrong"))
        }
    }

    private fun loadNextPage() {
        if (state.isLoading || state.endReached) return

        viewModelScope.launch(dispatcher) {
            try {
                state = state.copy(isLoading = true, error = null)

                if (state.query.isBlank()) {
                    val popularResp = popularMoviesRepo.getMovies(state.currentMoviePage)
                    val merged = state.movies + popularResp.results
                    val marked = applyFavoriteFlags(merged)

                    state = state.copy(
                        isLoading = false,
                        movies = marked,
                        currentMoviePage = state.currentMoviePage + 1,
                        endReached = popularResp.page >= popularResp.totalPages
                    )
                } else {
                    val moviesResp = searchRepo.searchMovies(state.query, state.currentMoviePage)
                    val showsResp = searchRepo.searchSeries(state.query, state.currentTVShowsPage)

                    val merged = state.movies + moviesResp.results + showsResp.results
                    val marked = applyFavoriteFlags(merged)

                    val endReached =
                        moviesResp.page >= moviesResp.totalPages &&
                                showsResp.page >= showsResp.totalPages

                    state = state.copy(
                        isLoading = false,
                        movies = marked,
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
            state = if (reset) MainState(isLoading = true)
            else state.copy(isLoading = true, error = null)

            val page = if (reset) 1 else state.currentMoviePage
            val resp = popularMoviesRepo.getMovies(page)

            val merged =
                if (reset) resp.results
                else state.movies + resp.results

            val marked = applyFavoriteFlags(merged)

            state = state.copy(
                isLoading = false,
                movies = marked,
                currentMoviePage = if (reset) SECOND_PAGE else nextPageAfterFirst(),
                currentTVShowsPage = 1,
                query = "",
                endReached = resp.page >= resp.totalPages
            )

        } catch (e: Exception) {
            state = state.copy(isLoading = false, error = e.message)
            _events.emit(MainUiEvent.ShowError(e.message ?: "Something went wrong"))
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch(dispatcher) {
            favoritesRepo.getFavorites().collect { favs ->
                favoriteIds = favs.map { it.id }.toSet()
                state = state.copy(
                    movies = applyFavoriteFlags(state.movies)
                )
            }
        }
    }
}

sealed interface MainUiEvent {
    data class ShowError(val message: String) : MainUiEvent
}
