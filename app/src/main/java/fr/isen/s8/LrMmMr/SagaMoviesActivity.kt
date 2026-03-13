package fr.isen.s8.LrMmMr

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import fr.isen.s8.LrMmMr.screens.SagaMoviesScreen
import fr.isen.s8.LrMmMr.ui.theme.TheAmazingDisneyAppTheme

class SagaMoviesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sagaName = intent.getStringExtra("SAGA_NAME") ?: ""
        val apiKey = "a4a738325f5cd022e712c9b94a94f34a"

        setContent {
            TheAmazingDisneyAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SagaMoviesScreen(
                        sagaName = sagaName,
                        apiKey = apiKey,
                        onMovieClick = { movieTitle ->
                            val intent = Intent(this, DetailledMovieActivity::class.java).apply {
                                putExtra("MOVIE_TITLE", movieTitle)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
