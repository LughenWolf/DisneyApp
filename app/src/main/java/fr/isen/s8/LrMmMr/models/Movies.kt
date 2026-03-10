package fr.isen.s8.LrMmMr.models

class Movies {

    data class Movie(
        val title: String,
        val imageUrl: String,
        val genre: String,
        val year: String,
        val universe: String,
        val saga: String,
        val number: String
    )
}
