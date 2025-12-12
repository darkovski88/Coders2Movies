package com.coders.two.movies.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteMovieEntity(
    @PrimaryKey val id: Int,
    val title: String?,
    val name: String?,
    val overview: String,
    val posterPath: String?,
    val firstAirDate: String?,
    val releaseDate: String?,
    val voteAverage: Double,
)