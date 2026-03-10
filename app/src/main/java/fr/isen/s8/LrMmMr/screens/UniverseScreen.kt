package fr.isen.s8.LrMmMr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import fr.isen.s8.LrMmMr.R
import fr.isen.s8.LrMmMr.components.ImageBannerCard
import fr.isen.s8.LrMmMr.components.SearchGlassField
import fr.isen.s8.LrMmMr.models.AppBarState


@Composable
fun UniversesScreen(modifier: Modifier = Modifier, onComposing: (AppBarState) -> Unit = {}) {
    val context = LocalContext.current

    val universes = remember {
        mutableStateOf(
            listOf(
                Universe("MCU", "Marvel Cinematic Universe"),
                Universe("Pixar", "Animation Studios"),
                Universe("Star Wars", "A long time ago...")
            )
        )
    }

    LaunchedEffect(Unit) {
        onComposing(AppBarState("Univers Disney"))
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        colorResource(R.color.egyptian_blue),
                        colorResource(R.color.glaucous)
                    )
                )
            ),
    ) {
        // L'utilisation d'une Column permet d'empiler verticalement
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Barre de recherche en haut
            SearchGlassField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = "Search"
            )

            // Liste en dessous
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(universes.value) { universe ->
                    ImageBannerCard(
                        title = universe.title,
                        imageRes = R.drawable.mcu,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /* Action */ }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun fonctionPreview() {
    UniversesScreen()
}

// Modèles et classes d'état
data class Universe(val title: String, val info: String)
