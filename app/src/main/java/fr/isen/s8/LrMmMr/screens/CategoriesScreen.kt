package fr.isen.s8.LrMmMr.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
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

@Composable
fun CategoriesScreen(franchiseName: String, onBackClick: () -> Unit) {
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
                fetchFranchiseImage(saga.nom, apiKey) { url ->
                    franchiseImages[saga.nom] = url
                }
            }
        }.addOnFailureListener {
            isLoading = false
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp, top = 32.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = franchiseName.uppercase(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else if (franchiseData != null) {
            val sousSagas = franchiseData?.sous_sagas ?: emptyList()
            
            if (sousSagas.isEmpty()) {
                Text("Aucune sous-saga trouvée", color = Color.White)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(sousSagas) { saga ->
                        ImageBannerCard(
                            title = saga.nom.uppercase(),
                            imageUrl = franchiseImages[saga.nom],
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { 
                                val intent = Intent(context, SagaMoviesActivity::class.java).apply {
                                    putExtra("SAGA_NAME", saga.nom)
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
