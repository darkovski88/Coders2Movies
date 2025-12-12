package com.coders.two.movies.presentation.main

import app.cash.turbine.test
import com.coders.two.movies.FakeConnectivityProvider
import com.coders.two.movies.FakeFavoritesRepo
import com.coders.two.movies.FakePopularRepo
import com.coders.two.movies.FakeSearchRepo
import com.coders.two.movies.data.model.MovieDto
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel

    private lateinit var popularRepo: FakePopularRepo
    private lateinit var searchRepo: FakeSearchRepo
    private lateinit var favoritesRepo: FakeFavoritesRepo
    private lateinit var connectivity: FakeConnectivityProvider

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        popularRepo = FakePopularRepo()
        searchRepo = FakeSearchRepo()
        favoritesRepo = FakeFavoritesRepo()
        connectivity = FakeConnectivityProvider(initial = true)

        viewModel = MainViewModel(
            popularMoviesRepo = popularRepo,
            searchRepo = searchRepo,
            favoritesRepo = favoritesRepo,
            connectivity = connectivity,
            dispatcher = dispatcher
        )
    }

    @Test
    fun `initial load online shows popular movies`() = runTest(dispatcher) {
        viewModel.onIntent(MainIntent.LoadInitial)
        advanceUntilIdle()

        assertFalse(viewModel.state.isOffline)
        assertEquals(5, viewModel.state.movies.size)
    }

    @Test
    fun `initial load offline shows favorites`() = runTest(dispatcher) {
        favoritesRepo.toggleFavorite(
            MovieDto(
                id = 1,
                title = "Fav",
                name = null,
                overview = "",
                posterPath = null,
                firstAirDate = null,
                releaseDate = "2020-01-01",
                voteAverage = 0.0
            )
        )

        connectivity.setConnected(false)
        advanceUntilIdle()

        viewModel.onIntent(MainIntent.LoadInitial)
        advanceUntilIdle()

        assertTrue(viewModel.state.isOffline)
        assertEquals(1, viewModel.state.movies.size)
        assertTrue(viewModel.state.movies.first().isFavorite)
    }

    @Test
    fun `offline to online reloads popular movies only after explicit reload`() =
        runTest(dispatcher) {

            connectivity.setConnected(false)
            viewModel.onIntent(MainIntent.LoadInitial)
            advanceUntilIdle()

            assertTrue(viewModel.state.isOffline)
            assertEquals(0, viewModel.state.movies.size)

            connectivity.setConnected(true)
            advanceUntilIdle()

            assertFalse(viewModel.state.isOffline)
            assertEquals(5, viewModel.state.movies.size)
        }

    @Test
    fun `pagination ignored when offline`() = runTest(dispatcher) {
        connectivity.setConnected(false)
        advanceUntilIdle()

        viewModel.onIntent(MainIntent.LoadInitial)
        advanceUntilIdle()

        val before = viewModel.state.movies

        viewModel.onIntent(MainIntent.LoadNextPage)
        advanceUntilIdle()

        assertEquals(before, viewModel.state.movies)
        assertTrue(viewModel.state.isOffline)
    }

    @Test
    fun `search returns movies and series when online`() = runTest(dispatcher) {
        viewModel.onIntent(MainIntent.Search("star"))
        advanceTimeBy(500)
        advanceUntilIdle()

        assertEquals(6, viewModel.state.movies.size)
        assertEquals(2, viewModel.state.currentMoviePage)
        assertEquals(2, viewModel.state.currentTVShowsPage)
    }

    @Test
    fun `search blocked when offline`() = runTest(dispatcher) {
        connectivity.setConnected(false)
        advanceUntilIdle()

        viewModel.events.test {
            viewModel.onIntent(MainIntent.Search("star"))
            advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is MainUiEvent.ShowError)
        }
    }

    @Test
    fun `toggling favorite updates UI`() = runTest(dispatcher) {
        viewModel.onIntent(MainIntent.LoadInitial)
        advanceUntilIdle()

        val movie = viewModel.state.movies.first()

        viewModel.onIntent(MainIntent.ToggleFavorite(movie))
        advanceUntilIdle()

        assertTrue(viewModel.state.movies.first().isFavorite)

        viewModel.onIntent(MainIntent.ToggleFavorite(movie))
        advanceUntilIdle()

        assertFalse(viewModel.state.movies.first().isFavorite)
    }

    @Test
    fun `search error emits UI event`() = runTest(dispatcher) {
        searchRepo.shouldThrow = true

        viewModel.events.test {
            viewModel.onIntent(MainIntent.Search("boom"))
            advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is MainUiEvent.ShowError)
        }
    }
}
