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


        val isUserConnected = true


        val mockUserUid = "test_user_12345"

        setContent {
            TheAmazingDisneyAppTheme {
                DetailledMovieScreen(
                    movieTitle = movieTitle,
                    apiKey = tmdbApiKey,
                    isConnected = isUserConnected,
                    userUid = mockUserUid
                )
            }
        }
    }
}