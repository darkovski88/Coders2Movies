package com.coders.two.movies.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.coders.two.movies.R
import com.coders.two.movies.data.model.MovieDto
import com.coders.two.movies.presentation.theme.Coders2MoviesTheme
import com.coders.two.movies.presentation.ui.IconText

@Composable
internal fun DetailsScreen(
    movie: MovieDto,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            model = ImageRequest.Builder(LocalContext.current).data(movie.fullPoster)
                .crossfade(true).build(),
            contentDescription = movie.displayTitle,
            error = painterResource(R.drawable.placeholder),
            placeholder = painterResource(R.drawable.placeholder),
            contentScale = ContentScale.Crop,
        )
        HorizontalDivider(thickness = 12.dp, color = Color.Transparent)
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = movie.displayTitle,
            fontSize = 32.sp,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline,
            fontWeight = Bold,
            maxLines = 2,
            lineHeight = 36.sp,
            overflow = TextOverflow.Ellipsis
        )
        HorizontalDivider(thickness = 12.dp, color = Color.Transparent)
        IconText(
            text = stringResource(R.string.release_date, movie.displayDate),
            icon = Icons.Default.DateRange
        )
        IconText(
            text = stringResource(R.string.average_rating, movie.voteAverage),
            icon = Icons.Default.Star
        )
        HorizontalDivider(thickness = 24.dp, color = Color.Transparent)
        Text(text = movie.overview, modifier = Modifier.padding(horizontal = 8.dp))
    }
}

@PreviewLightDark
@Composable
private fun DetailsPreview() {
    val movie = MovieDto(
        id = 123,
        title = "Some extralong title for a movie - looks insane",
        name = null,
        overview = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
        posterPath = "/1RgPyOhN4DRs225BGTlHJqCudII.jpg",
        voteAverage = 7.61,
        releaseDate = "2025-07-18",
        firstAirDate = null,
    )
    Coders2MoviesTheme {
        Surface {
            DetailsScreen(movie)
        }
    }
}