package fr.isen.s8.LrMmMr.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import fr.isen.s8.LrMmMr.R
import fr.isen.s8.LrMmMr.models.FirebaseCategory
import fr.isen.s8.LrMmMr.models.Movies.Movie

@Composable
fun SagaMoviesScreen(sagaName: String, apiKey: String, onMovieClick: (String) -> Unit) {
    var moviesList by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(sagaName) {
        val database = Firebase.database.reference.child("categories")
        database.get().addOnSuccessListener { snapshot ->
            val categories = snapshot.children.mapNotNull { it.getValue<FirebaseCategory>() }
            
            // On cherche tous les films qui appartiennent à cette saga (sous-saga)
            val filteredMovies = mutableListOf<Movie>()
            for (cat in categories) {
                for (fran in cat.franchises) {
                    fran.sous_sagas?.find { it.nom == sagaName }?.let { saga ->
                        saga.films.forEach { film ->
                            filteredMovies.add(
                                Movie(film.titre, "", film.genre, film.annee.toString(), fran.nom, saga.nom, "N° ${film.numero}")
                            )
                        }
                    }
                }
            }
            moviesList = filteredMovies.sortedBy { it.number.replace("N° ", "").toIntOrNull() ?: 0 }
            isLoading = false
        }.addOnFailureListener {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(colorResource(R.color.egyptian_blue), colorResource(R.color.glaucous))))
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = sagaName.uppercase(),
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
            } else if (moviesList.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun film trouvé", color = Color.White)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(moviesList) { movie ->
                        MovieGridItem(
                            movie = movie,
                            apiKey = apiKey,
                            imageCache = mutableMapOf<String, String?>(),
                            onClick = { onMovieClick(movie.title) }
                        )
                    }
                }
            }
        }
    }
}
