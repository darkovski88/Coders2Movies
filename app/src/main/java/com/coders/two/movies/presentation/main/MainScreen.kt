package com.coders.two.movies.presentation.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.coders.two.movies.data.model.MovieDto

private const val ItemsLoadThreshold = 1

@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    onItemClicked: (MovieDto) -> Unit
) {
    val state = viewModel.state
    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        viewModel.onIntent(MainIntent.LoadNextPage)
    }

    Box(modifier = modifier.fillMaxSize()) {
        LaunchedEffect(listState) {
            snapshotFlow {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                lastVisible == listState.layoutInfo.totalItemsCount - ItemsLoadThreshold
            }.collect { isAtBottom ->
                if (isAtBottom) {
                    viewModel.onIntent(MainIntent.LoadNextPage)
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items = state.movies) { movie ->
                MovieRow(movie = movie) {
                    onItemClicked(it)
                }
            }
        }

        AnimatedVisibility(state.isLoading, enter = fadeIn(), exit = fadeOut()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = .5f))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center),
                    trackColor = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}
