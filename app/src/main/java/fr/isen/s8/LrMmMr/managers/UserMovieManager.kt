package fr.isen.s8.LrMmMr.managers

import com.google.firebase.Firebase
import com.google.firebase.database.database

object UserMovieManager {


    fun toggleMovieStatus(uid: String, movieTitle: String, listType: String, isAdded: Boolean) {
        val databaseRef = Firebase.database.reference
            .child("users")
            .child(uid)
            .child(listType)
            .child(movieTitle)

        if (isAdded) {

            databaseRef.setValue(true)
        } else {

            databaseRef.removeValue()


            if (listType == "own") {
                Firebase.database.reference
                    .child("users")
                    .child(uid)
                    .child("wantToGetRidOf")
                    .child(movieTitle)
                    .removeValue()
            }

        }
    }
}