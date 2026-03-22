package fr.isen.s8.LrMmMr.dataClasses

import com.google.gson.annotations.SerializedName

data class MovieSearchResponse(
    @SerializedName("results")
    val results: List<MoviePreview>?
)

data class MoviePreview(
    @SerializedName("id")
    val id: Int,
    @SerializedName("poster_path")
    val posterPath: String?,
    @SerializedName("release_date")
    val releaseDate: String?
)