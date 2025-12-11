package com.coders.two.movies.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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

@Composable
internal fun MovieRow(
    movie: MovieDto,
    onFavorite: (MovieDto) -> Unit,
    onItemClick: (MovieDto) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(movie)
            }
            .padding(12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.posterThumb)
                .crossfade(true)
                .build(),
            contentDescription = movie.displayTitle,
            error = painterResource(R.drawable.placeholder),
            placeholder = painterResource(R.drawable.placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
        )
        VerticalDivider(thickness = 12.dp)
        Column(Modifier.weight(1f)) {
            Text(
                movie.displayTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline
            )
            VerticalDivider(thickness = 12.dp)
            Text(movie.overview, maxLines = 3, overflow = TextOverflow.Ellipsis)
        }

        IconButton(onClick = { onFavorite(movie) }) {
            Icon(
                imageVector = if (movie.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                tint = Color.Red,
                contentDescription = null
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun MovieRowPreview() {
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
            MovieRow(movie, {}) { }
        }
    }
}