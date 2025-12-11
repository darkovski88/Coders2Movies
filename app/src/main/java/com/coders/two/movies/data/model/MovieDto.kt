package com.coders.two.movies.data.model

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @Json(name = "poster_path")
    val posterPath: String
) : Parcelable {
    val posterThumb = "https://image.tmdb.org/t/p/w200$posterPath"
    val fullPoster = "https://image.tmdb.org/t/p/w500$posterPath"
}