package fr.isen.s8.LrMmMr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import fr.isen.s8.LrMmMr.screens.UniversesScreen
import fr.isen.s8.LrMmMr.ui.theme.TheAmazingDisneyAppTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // État pour gérer le titre
            var title by remember { mutableStateOf("Films Disney") }

            TheAmazingDisneyAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text(title) }
                        )
                    }
                ) { innerPadding ->
                    UniversesScreen(
                        modifier = Modifier.padding(innerPadding),
                        onComposing = { appBarState ->
                            title = appBarState.title
                        }
                    )
                }
            }
        }
    }
}
