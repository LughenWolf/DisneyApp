package fr.isen.s8.LrMmMr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import fr.isen.s8.LrMmMr.screens.DetailledMovieScreen
import fr.isen.s8.LrMmMr.ui.theme.TheAmazingDisneyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Le titre qu'on veut tester.
        val selectedTitle = "Alien, le Huitième Passager"
        val myApiKey = "a4a738325f5cd022e712c9b94a94f34a"

        // 1. On crée une variable pour simuler la connexion
        val isUserConnected = true

        setContent {
            TheAmazingDisneyAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        // 2. On n'oublie pas de passer l'état de connexion à l'écran !
                        DetailledMovieScreen(
                            movieTitle = selectedTitle,
                            apiKey = myApiKey,
                            isConnected = isUserConnected
                        )
                    }
                }
            }
        }
    }
}