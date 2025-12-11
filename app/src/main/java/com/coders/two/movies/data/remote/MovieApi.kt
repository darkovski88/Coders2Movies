package com.coders.two.movies.data.remote

import com.coders.two.movies.data.model.MovieResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = "0c8a4d24efe6b43ab1fb44d3e0e075ba",
        @Query("page") page: Int,
    ): MovieResponse
}
