package com.example.moviereviewapp.api.service

import com.example.moviereviewapp.model.MovieFullDetail
import com.example.moviereviewapp.model.MovieListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("3/movie/{Type}")
    suspend fun getMoviesList(
        @Path("Type") type: String,
        @Query("api_key") key: String,
        @Query("page") page: Int
    ): Response<MovieListResponse>

    @GET("3/movie/{MovieId}")
    suspend fun getMovieDetail(
        @Path("MovieId") movieId: Int,
        @Query("api_key") key: String,
        @Query("session_id") id: String,
        @Query("append_to_response") query: String = "videos,credits,account_states,recommendations",
        @Query("language") language: String = "en-US"
    ) : Response<MovieFullDetail>

}