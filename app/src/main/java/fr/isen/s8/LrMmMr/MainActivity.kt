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

        val selectedTitle = "Deadpool"
        val myApiKey = "a4a738325f5cd022e712c9b94a94f34a"
        val isUserConnected = true

        // NOTRE FAUX UID POUR TESTER
        val mockUserUid = "test_user_12345"

        setContent {
            TheAmazingDisneyAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        DetailledMovieScreen(
                            movieTitle = selectedTitle,
                            apiKey = myApiKey,
                            isConnected = isUserConnected,
                            userUid = mockUserUid // On passe l'UID ici !
                        )
                    }
                }
            }
        }
    }
}