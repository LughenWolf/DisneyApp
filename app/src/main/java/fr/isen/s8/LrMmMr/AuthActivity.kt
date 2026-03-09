package fr.isen.s8.LrMmMr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import fr.isen.s8.LrMmMr.screens.AuthScreen
import fr.isen.s8.LrMmMr.ui.theme.TheAmazingDisneyAppTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TheAmazingDisneyAppTheme {
                AuthScreen(onSuccess = { finish() })
            }
        }
    }
}