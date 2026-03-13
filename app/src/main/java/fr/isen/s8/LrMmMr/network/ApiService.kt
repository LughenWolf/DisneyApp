package fr.isen.s8.LrMmMr.network

import fr.isen.s8.LrMmMr.dataClasses.MovieSearchResponse
import fr.isen.s8.LrMmMr.dataClasses.VideoResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("3/search/movie")
    fun searchMovie(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("language") language: String = "fr-FR"
    ): Call<MovieSearchResponse>

    // --- NOUVELLE REQUÊTE POUR LES VIDÉOS ---
    @GET("3/movie/{movie_id}/videos")
    fun getMovieVideos(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "fr-FR"
    ): Call<VideoResponse>
}