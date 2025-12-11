package com.coders.two.movies.presentation.main

import app.cash.turbine.test
import com.coders.two.movies.FakeFavoritesRepo
import com.coders.two.movies.FakePopularRepo
import com.coders.two.movies.FakeSearchRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var vm: MainViewModel
    private lateinit var fakeFavoritesRepo: FakeFavoritesRepo
    private lateinit var fakePopularRepo: FakePopularRepo
    private lateinit var fakeSearchRepo: FakeSearchRepo

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        fakePopularRepo = FakePopularRepo()
        fakeSearchRepo = FakeSearchRepo()
        fakeFavoritesRepo = FakeFavoritesRepo()

        vm = MainViewModel(
            popularMoviesRepo = fakePopularRepo,
            searchRepo = fakeSearchRepo,
            dispatcher = dispatcher,
            favoritesRepo = fakeFavoritesRepo
        )
    }

    @Test
    fun `initial load gets popular movies`() = runTest(dispatcher) {
        vm.onIntent(MainIntent.LoadInitial)

        advanceUntilIdle()

        assertEquals(5, vm.state.movies.size)
        assertEquals(2, vm.state.currentMoviePage)
        assertFalse(vm.state.isLoading)
    }

    @Test
    fun `search returns movies plus series`() = runTest(dispatcher) {
        vm.onIntent(MainIntent.Search("star"))

        advanceUntilIdle()

        assertEquals(6, vm.state.movies.size) // 3 movies + 3 shows
        assertEquals(2, vm.state.currentMoviePage)
        assertEquals(2, vm.state.currentTVShowsPage)
    }

    @Test
    fun `next page loads additional search results`() = runTest(dispatcher) {
        vm.onIntent(MainIntent.Search("star"))
        advanceUntilIdle()

        val firstPage = vm.state.movies.size

        vm.onIntent(MainIntent.LoadNextPage)
        advanceUntilIdle()

        assertTrue(vm.state.movies.size > firstPage)
        assertEquals(3, vm.state.currentMoviePage)
        assertEquals(3, vm.state.currentTVShowsPage)
    }

    @Test
    fun `clearing search restores popular movies`() = runTest(dispatcher) {
        vm.onIntent(MainIntent.Search("abc"))

        advanceTimeBy(500)
        advanceUntilIdle()

        assertEquals(6, vm.state.movies.size)

        vm.onIntent(MainIntent.Search(""))

        advanceTimeBy(500)
        advanceUntilIdle()

        assertEquals(5, vm.state.movies.size)
    }

    @Test
    fun `error emits UI event`() = runTest(dispatcher) {
        fakeSearchRepo.shouldThrow = true

        vm.events.test {
            vm.onIntent(MainIntent.Search("boom"))
            advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is MainUiEvent.ShowError)
        }
    }

    @Test
    fun `toggling favorites updates UI state`() = runTest(dispatcher) {
        vm.onIntent(MainIntent.LoadInitial)
        advanceUntilIdle()

        val movie = vm.state.movies.first()

        vm.onIntent(MainIntent.ToggleFavorite(movie))
        advanceUntilIdle()

        assertTrue(vm.state.movies.first().isFavorite)

        vm.onIntent(MainIntent.ToggleFavorite(movie))
        advanceUntilIdle()

        assertFalse(vm.state.movies.first().isFavorite)
    }
}
