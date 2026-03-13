package fr.isen.s8.LrMmMr.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import fr.isen.s8.LrMmMr.CategoriesActivity
import fr.isen.s8.LrMmMr.R
import fr.isen.s8.LrMmMr.components.ImageBannerCard
import fr.isen.s8.LrMmMr.components.SearchGlassField
import fr.isen.s8.LrMmMr.models.AppBarState
import fr.isen.s8.LrMmMr.models.FirebaseCategory
import fr.isen.s8.LrMmMr.models.FirebaseFranchise
import fr.isen.s8.LrMmMr.network.ApiClient
import fr.isen.s8.LrMmMr.dataClasses.MovieSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun UniversesScreen(modifier: Modifier = Modifier, onComposing: (AppBarState) -> Unit = {}) {
    var franchises by remember { mutableStateOf<List<FirebaseFranchise>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // On utilise une Map pour stocker les URLs des images récupérées par franchise
    val franchiseImages = remember { mutableStateMapOf<String, String>() }
    val apiKey = "a4a738325f5cd022e712c9b94a94f34a" // Utilisation de votre clé TMDB

    LaunchedEffect(Unit) {
        onComposing(AppBarState("Univers Disney"))

        val database = Firebase.database.reference.child("categories")
        database.get().addOnSuccessListener { snapshot ->
            val categories = snapshot.children.mapNotNull { it.getValue<FirebaseCategory>() }
            val allFranchises = categories.flatMap { it.franchises }
            franchises = allFranchises
            isLoading = false

            // Pour chaque franchise, on va chercher une image sur TMDB
            allFranchises.forEach { franchise ->
                fetchFranchiseImage(franchise.nom, apiKey) { url ->
                    franchiseImages[franchise.nom] = url
                }
            }
        }.addOnFailureListener {
            isLoading = false
        }
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
        Column(modifier = Modifier.fillMaxSize()) {
            SearchGlassField(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = "Rechercher un film..."
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(franchises) { franchise ->
                        ImageBannerCard(
                            title = franchise.nom.uppercase(),
                            // On utilise l'URL TMDB si on l'a trouvée, sinon l'image par défaut
                            imageUrl = franchiseImages[franchise.nom],
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val intent = Intent(context, CategoriesActivity::class.java).apply {
                                    putExtra("FRANCHISE_NAME", franchise.nom)
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fonction pour chercher une image de "fond" ou un poster pour une franchise donnée
 */
fun fetchFranchiseImage(query: String, apiKey: String, onResult: (String) -> Unit) {
    ApiClient.retrofit.searchMovie(apiKey = apiKey, query = query)
        .enqueue(object : Callback<MovieSearchResponse> {
            override fun onResponse(call: Call<MovieSearchResponse>, response: Response<MovieSearchResponse>) {
                val path = response.body()?.results?.firstOrNull()?.posterPath
                if (path != null) {
                    onResult("https://image.tmdb.org/t/p/w500$path")
                }
            }
            override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) {
                // Erreur ignorée, l'image par défaut sera utilisée
            }
        })
}
