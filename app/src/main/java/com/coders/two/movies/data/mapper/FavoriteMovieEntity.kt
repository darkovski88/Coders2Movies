package com.coders.two.movies.data.mapper

import com.coders.two.movies.data.db.FavoriteMovieEntity
import com.coders.two.movies.data.model.MovieDto

fun FavoriteMovieEntity.toMovieDto(): MovieDto {
    return MovieDto(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        name = name,
        firstAirDate = firstAirDate,
        isFavorite = true,
    )
}