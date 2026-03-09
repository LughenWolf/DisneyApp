package fr.isen.s8.LrMmMr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import fr.isen.s8.LrMmMr.screens.DetailledMovieScreen
import fr.isen.s8.LrMmMr.ui.theme.TheAmazingDisneyAppTheme

class DetailledMovieActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val movieTitle = intent.getStringExtra("MOVIE_TITLE")!!

        val tmdbApiKey = "a4a738325f5cd022e712c9b94a94f34a"

        // 1. On crée une variable pour simuler la connexion.
        // Plus tard, on remplacera ce "true" par une vraie vérification Firebase.
        // Essaie de changer ce "true" en "false" pour voir les icônes disparaître !
        val isUserConnected = true

        setContent {
            TheAmazingDisneyAppTheme {
                DetailledMovieScreen(
                    movieTitle = movieTitle,
                    apiKey = tmdbApiKey,
                    isConnected = isUserConnected // 2. On passe l'information à l'écran
                )
            }
        }
    }
}