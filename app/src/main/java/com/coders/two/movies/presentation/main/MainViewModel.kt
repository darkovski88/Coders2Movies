package com.coders.two.movies.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coders.two.movies.core.ConnectivityProvider
import com.coders.two.movies.data.mapper.toMovieDto
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val FIRST_PAGE = 1
private const val SECOND_PAGE = FIRST_PAGE + 1

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val popularMoviesRepo: PopularMoviesRepository,
    private val searchRepo: SearchRepository,
    private val favoritesRepo: FavoritesRepository,
    private val connectivity: ConnectivityProvider,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private var favoriteIds: Set<Int> = emptySet()

    var state by mutableStateOf(MainState())
        private set

    private val queryFlow = MutableStateFlow("")
    private val _events = MutableSharedFlow<MainUiEvent>()
    val events = _events.asSharedFlow()

    init {
        observeConnectivity()
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

    private fun observeConnectivity() {
        viewModelScope.launch(dispatcher) {
            connectivity.isConnected
                .distinctUntilChanged()
                .collect { connected ->

                    val wasOffline = state.isOffline
                    state = state.copy(isOffline = !connected)

                    when {
                        !connected -> {
                            loadFavoritesFallback()
                        }

                        connected && wasOffline -> {
                            loadPopularPage(reset = true)
                        }
                    }
                }
        }
    }

    private fun loadInitial() {
        viewModelScope.launch(dispatcher) {
            if (!connectivity.isCurrentlyConnected()) {
                loadFavoritesFallback()
            } else {
                loadPopularPage(reset = true)
            }
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
                        if (connectivity.isCurrentlyConnected()) {
                            loadPopularPage(reset = true)
                        }
                    } else {
                        if (!connectivity.isCurrentlyConnected()) {
                            _events.emit(
                                MainUiEvent.ShowError("Offline – search unavailable")
                            )
                            return@collect
                        }
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
            e.printStackTrace()
            state = state.copy(isLoading = false, error = e.message)
            _events.emit(MainUiEvent.ShowError("Search failed"))
        }
    }

    private fun loadNextPage() {
        if (state.isLoading || state.endReached) return
        if (!connectivity.isCurrentlyConnected()) return

        viewModelScope.launch(dispatcher) {
            try {
                state = state.copy(isLoading = true)

                if (state.query.isBlank()) {
                    val resp = popularMoviesRepo.getMovies(state.currentMoviePage)
                    state = state.copy(
                        isLoading = false,
                        movies = applyFavoriteFlags(state.movies + resp.results),
                        currentMoviePage = state.currentMoviePage + 1,
                        endReached = resp.page >= resp.totalPages
                    )
                } else {
                    val moviesResp =
                        searchRepo.searchMovies(state.query, state.currentMoviePage)
                    val showsResp =
                        searchRepo.searchSeries(state.query, state.currentTVShowsPage)

                    val merged = applyFavoriteFlags(
                        state.movies + moviesResp.results + showsResp.results
                    )

                    val endReached =
                        moviesResp.page >= moviesResp.totalPages &&
                                showsResp.page >= showsResp.totalPages

                    state = state.copy(
                        isLoading = false,
                        movies = merged,
                        currentMoviePage = state.currentMoviePage + 1,
                        currentTVShowsPage = state.currentTVShowsPage + 1,
                        endReached = endReached
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                state = state.copy(isLoading = false)
                _events.emit(MainUiEvent.ShowError("Loading failed"))
            }
        }
    }

    private suspend fun loadPopularPage(reset: Boolean) {
        try {
            val page = if (reset) FIRST_PAGE else state.currentMoviePage
            val resp = popularMoviesRepo.getMovies(page)

            val merged =
                if (reset) resp.results
                else state.movies + resp.results

            state = state.copy(
                isLoading = false,
                movies = applyFavoriteFlags(merged),
                currentMoviePage = if (reset) SECOND_PAGE else state.currentMoviePage + 1,
                currentTVShowsPage = 1,
                query = "",
                endReached = resp.page >= resp.totalPages
            )
        } catch (e: Exception) {
            e.printStackTrace()
            loadFavoritesFallback()
        }
    }

    private suspend fun loadFavoritesFallback() {
        val favorites = favoritesRepo.getFavorites().first()

        state = state.copy(
            isLoading = false,
            isOffline = true,
            movies = favorites.map { it.toMovieDto() },
            query = "",
            endReached = true,
            error = "Offline – showing favorites"
        )
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

    private fun toggleFavorite(movie: MovieDto) {
        viewModelScope.launch(dispatcher) {
            favoritesRepo.toggleFavorite(movie)
        }
    }

    private fun applyFavoriteFlags(list: List<MovieDto>): List<MovieDto> =
        list.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
}

sealed interface MainUiEvent {
    data class ShowError(val message: String) : MainUiEvent
}
