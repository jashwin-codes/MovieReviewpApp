package com.example.moviereviewapp.repository

import com.example.moviereviewapp.api.ServiceProvider
import com.example.moviereviewapp.api.service.MovieService
import com.example.moviereviewapp.utils.Constants
import com.example.moviereviewapp.utils.SessionManager

class MovieRepository : Repository() {
    private val movieService: MovieService = ServiceProvider.movieService
    suspend fun getMoviesList(type: String, page: Int = 1) =
        safeApiCall { movieService.getMoviesList(type, Constants.KEY, page) }

    suspend fun getMovieDetails(id: Int, sessionId: String) =
        safeApiCall { movieService.getMovieDetail( id,Constants.KEY,sessionId) }

}