package fr.isen.s8.LrMmMr.dataClasses


data class VideoResponse(
    val results: List<VideoResult>
)

data class VideoResult(
    val site: String,
    val type: String,
    val key: String
)