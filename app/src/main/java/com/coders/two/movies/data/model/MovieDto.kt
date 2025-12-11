package com.coders.two.movies.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDto(
    val id: Int,
    val title: String?,
    val name: String?,
    val overview: String,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "first_air_date")
    val firstAirDate: String?,
    @Json(name = "release_date")
    val releaseDate: String?,
    @Json(name = "vote_average")
    val voteAverage: Double,
    val isFavorite: Boolean = false
) : Parcelable {
    @IgnoredOnParcel
    val displayTitle = title ?: name ?: ""

    @IgnoredOnParcel
    val displayDate = releaseDate ?: firstAirDate ?: ""

    @IgnoredOnParcel
    val posterThumb = "https://image.tmdb.org/t/p/w200$posterPath"

    @IgnoredOnParcel
    val fullPoster = "https://image.tmdb.org/t/p/w500$posterPath"
}