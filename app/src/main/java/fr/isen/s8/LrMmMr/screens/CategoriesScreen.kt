package fr.isen.s8.LrMmMr.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import fr.isen.s8.LrMmMr.SagaMoviesActivity
import fr.isen.s8.LrMmMr.components.ImageBannerCard
import fr.isen.s8.LrMmMr.models.FirebaseCategory
import fr.isen.s8.LrMmMr.models.FirebaseFranchise
import fr.isen.s8.LrMmMr.network.ApiClient
import fr.isen.s8.LrMmMr.dataClasses.MovieSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun CategoriesScreen(franchiseName: String) {
    var franchiseData by remember { mutableStateOf<FirebaseFranchise?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val franchiseImages = remember { mutableStateMapOf<String, String>() }
    val apiKey = "a4a738325f5cd022e712c9b94a94f34a"
    val context = LocalContext.current

    LaunchedEffect(franchiseName) {
        val database = Firebase.database.reference.child("categories")
        database.get().addOnSuccessListener { snapshot ->
            val categories = snapshot.children.mapNotNull { it.getValue<FirebaseCategory>() }
            val foundFranchise = categories.flatMap { it.franchises }.find { it.nom == franchiseName }
            
            franchiseData = foundFranchise
            isLoading = false

            foundFranchise?.sous_sagas?.forEach { saga ->
                if (saga.films.isNotEmpty()) {
                    val firstMovieTitle = saga.films.firstOrNull()?.titre
                    if (firstMovieTitle != null) {
                        fetchFranchiseImage(firstMovieTitle, apiKey) { url ->
                            franchiseImages[saga.nom] = url
                        }
                    }
                }
            }
        }.addOnFailureListener {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = franchiseName.uppercase(),
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            modifier = Modifier.padding(bottom = 24.dp, top = 32.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (franchiseData != null) {
            val validSousSagas = franchiseData?.sous_sagas?.filter { it.films.isNotEmpty() } ?: emptyList()
            
            if (validSousSagas.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucune saga disponible pour le moment", color = Color.White)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(validSousSagas) { saga ->
                        ImageBannerCard(
                            title = saga.nom.uppercase(),
                            imageUrl = franchiseImages[saga.nom],
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { 
                                val intent = Intent(context, SagaMoviesActivity::class.java).apply {
                                    putExtra("SAGA_NAME", saga.nom)
                                    putExtra("FRANCHISE_NAME", franchiseName) // Ajout de la franchise pour l'unicité
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        } else {
            Text("Franchise introuvable", color = Color.White)
        }
    }
}
