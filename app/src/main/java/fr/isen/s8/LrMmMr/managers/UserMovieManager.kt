package fr.isen.s8.LrMmMr.managers



import com.google.firebase.Firebase
import com.google.firebase.database.database

object UserMovieManager {

    /**
     * Ajoute ou supprime un film d'une des 4 listes de l'utilisateur
     * @param uid L'ID unique de l'utilisateur
     * @param movieTitle Le titre du film
     * @param listType Le nom du noeud ("wantToWatch", "watched", "own", "wantToGetRidOf")
     * @param isAdded true pour ajouter, false pour supprimer
     */
    fun toggleMovieStatus(uid: String, movieTitle: String, listType: String, isAdded: Boolean) {
        val databaseRef = Firebase.database.reference
            .child("users")
            .child(uid)
            .child(listType)
            .child(movieTitle)

        if (isAdded) {
            // On sauvegarde la valeur "true" pour dire qu'il est dans la liste
            databaseRef.setValue(true)
        } else {
            // On retire le film de la liste en supprimant le noeud
            databaseRef.removeValue()
        }
    }
}