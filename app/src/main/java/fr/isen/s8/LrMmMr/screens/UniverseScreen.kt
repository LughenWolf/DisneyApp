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
import fr.isen.s8.LrMmMr.SagaMoviesActivity
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
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    val franchiseImages = remember { mutableStateMapOf<String, String>() }
    val apiKey = "a4a738325f5cd022e712c9b94a94f34a"

    LaunchedEffect(Unit) {
        onComposing(AppBarState("Univers Disney"))

        val database = Firebase.database.reference.child("categories")
        database.get().addOnSuccessListener { snapshot ->
            val categories = snapshot.children.mapNotNull { it.getValue<FirebaseCategory>() }
            
            // On filtre pour ne garder que les franchises qui ont au moins une sous-saga avec des films
            // OU au moins un film direct.
            val allFranchises = categories.flatMap { it.franchises }.filter { franchise ->
                val hasDirectFilms = franchise.films?.isNotEmpty() == true
                val hasValidSagas = franchise.sous_sagas?.any { it.films.isNotEmpty() } == true
                hasDirectFilms || hasValidSagas
            }
            
            franchises = allFranchises
            isLoading = false

            allFranchises.forEach { franchise ->
                fetchFranchiseImage(franchise.nom, apiKey) { url ->
                    franchiseImages[franchise.nom] = url
                }
            }
        }.addOnFailureListener {
            isLoading = false
        }
    }

    val filteredFranchises = remember(searchQuery, franchises) {
        if (searchQuery.isEmpty()) {
            franchises
        } else {
            franchises.filter { it.nom.contains(searchQuery, ignoreCase = true) }
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
                query = searchQuery,
                onQueryChange = { searchQuery = it },
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
                    items(filteredFranchises) { franchise ->
                        var posterUrl by remember(franchise.nom) { mutableStateOf<String?>(null) }

                        LaunchedEffect(franchise.nom) {
                            fetchFranchiseImage(franchise.nom, apiKey) { url ->
                                posterUrl = url
                            }
                        }

                        ImageBannerCard(
                            title = franchise.nom.uppercase(),
                            imageUrl = posterUrl,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                val validSagas = franchise.sous_sagas?.filter { it.films.isNotEmpty() } ?: emptyList()
                                
                                // Si une seule saga et pas de films directs, on skip l'écran de catégories
                                if (validSagas.size == 1 && (franchise.films == null || franchise.films.isEmpty())) {
                                    val intent = Intent(context, SagaMoviesActivity::class.java).apply {
                                        putExtra("SAGA_NAME", validSagas[0].nom)
                                        putExtra("FRANCHISE_NAME", franchise.nom)
                                    }
                                    context.startActivity(intent)
                                } else {
                                    val intent = Intent(context, CategoriesActivity::class.java).apply {
                                        putExtra("FRANCHISE_NAME", franchise.nom)
                                    }
                                    context.startActivity(intent)
                                }
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
            }
        })
}
