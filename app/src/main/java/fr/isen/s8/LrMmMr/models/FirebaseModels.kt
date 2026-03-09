package fr.isen.s8.LrMmMr.models


data class FirebaseCategory(
    val categorie: String = "",
    val franchises: List<FirebaseFranchise> = emptyList()
)

data class FirebaseFranchise(
    val nom: String = "",
    val films: List<FirebaseFilm>? = null,
    val sous_sagas: List<FirebaseSousSaga>? = null
)

data class FirebaseSousSaga(
    val nom: String = "",
    val films: List<FirebaseFilm> = emptyList()
)

data class FirebaseFilm(
    val titre: String = "",
    val annee: Int = 0,
    val genre: String = "",
    val numero: Int = 0
)

