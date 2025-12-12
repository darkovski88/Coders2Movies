package com.coders.two.movies.presentation.main

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.coders.two.movies.R
import com.coders.two.movies.data.model.MovieDto
import com.coders.two.movies.presentation.ui.MovieRow
import kotlinx.coroutines.flow.distinctUntilChanged

private const val ItemsLoadThreshold = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    onItemClicked: (MovieDto) -> Unit
) {
    val state = viewModel.state
    val listState = rememberLazyListState()
    val context = LocalContext.current

    val query = state.query

    LaunchedEffect(Unit) {
        viewModel.onIntent(MainIntent.LoadInitial)
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0 }
            .distinctUntilChanged()
            .collect { lastVisible ->
                val total = listState.layoutInfo.totalItemsCount
                if (!state.isOffline && total > 0 && lastVisible >= total - ItemsLoadThreshold
                ) {
                    viewModel.onIntent(MainIntent.LoadNextPage)
                }
            }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is MainUiEvent.ShowError ->
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SearchTopBar(
                query = query,
                enabled = !state.isOffline,
                onQueryChange = {
                    viewModel.onIntent(MainIntent.Search(it))
                }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(items = state.movies) { movie ->
                    MovieRow(
                        movie = movie,
                        onFavorite = {
                            viewModel.onIntent(MainIntent.ToggleFavorite(it))
                        }) {
                        onItemClicked(
                            it
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
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

@Composable
fun SearchTopBar(
    query: String,
    enabled: Boolean,
    onQueryChange: (String) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            enabled = enabled,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.search)
                )
            },
            placeholder = {
                Text(
                    text = stringResource(if (enabled) R.string.search else R.string.search_not_available_showing_favorites),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            singleLine = true,
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}