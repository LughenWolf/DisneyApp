package fr.isen.s8.LrMmMr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import fr.isen.s8.LrMmMr.screens.AuthScreen
import fr.isen.s8.LrMmMr.ui.theme.TheAmazingDisneyAppTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TheAmazingDisneyAppTheme {
                AuthScreen(
                    onSuccess = { finish() },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}