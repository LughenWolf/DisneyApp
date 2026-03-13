package fr.isen.s8.LrMmMr.dataClasses


import com.google.gson.annotations.SerializedName

data class VideoResponse(
    val results: List<VideoResult>
)

data class VideoResult(
    val site: String,
    val type: String,
    val key: String
)