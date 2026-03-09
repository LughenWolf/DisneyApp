package fr.isen.s8.LrMmMr.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

import com.google.firebase.Firebase
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import fr.isen.s8.LrMmMr.R
import fr.isen.s8.LrMmMr.models.Movies.Movie
import fr.isen.s8.LrMmMr.models.FirebaseCategory
import fr.isen.s8.LrMmMr.network.ApiClient
import fr.isen.s8.LrMmMr.dataClasses.MovieSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import fr.isen.s8.LrMmMr.components.GlassyCardTag
import fr.isen.s8.LrMmMr.components.GlassyInfoCard
import fr.isen.s8.LrMmMr.components.CustomTopBar

@Composable
fun DetailledMovieScreen(movieTitle: String, apiKey: String, isConnected: Boolean) {
    var movieDetails by remember { mutableStateOf<Movie?>(null) }
    var tmdbImageUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(movieTitle) {
        val database = Firebase.database.reference.child("categories")

        database.get().addOnSuccessListener { snapshot ->
            val categories = snapshot.children.mapNotNull { it.getValue<FirebaseCategory>() }
            val found = findMovieInCategories(categories, movieTitle)

            if (found != null) {
                movieDetails = found

                ApiClient.retrofit.searchMovie(apiKey = apiKey, query = movieTitle)
                    .enqueue(object : Callback<MovieSearchResponse> {
                        override fun onResponse(call: Call<MovieSearchResponse>, response: Response<MovieSearchResponse>) {
                            val path = response.body()?.results?.firstOrNull()?.posterPath
                            if (path != null) {
                                tmdbImageUrl = "https://image.tmdb.org/t/p/w500$path"
                            }
                            isLoading = false
                        }
                        override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) {
                            isLoading = false
                        }
                    })
            } else {
                isLoading = false
            }
        }.addOnFailureListener {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(colorResource(R.color.egyptian_blue), colorResource(R.color.glaucous))))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            CustomTopBar(
                movieTitle = movieTitle,
                isConnected = isConnected,
                onWantToWatchClick = { },
                onWatchClick = { },
                onOwnClick = { },
                onDeleteClick = { }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                } else if (movieDetails != null) {
                    DetailledMovie(movie = movieDetails!!.copy(imageUrl = tmdbImageUrl ?: ""), apiKey = apiKey)
                } else {
                    Text("Film introuvable", color = Color.White, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

private fun findMovieInCategories(categories: List<FirebaseCategory>, title: String): Movie? {
    for (cat in categories) {
        for (fran in cat.franchises) {
            fran.films?.find { it.titre.equals(title, ignoreCase = true) }?.let {
                return Movie(it.titre, "", it.genre, it.annee.toString(), cat.categorie, fran.nom, "N° ${it.numero}")
            }
            fran.sous_sagas?.forEach { saga ->
                saga.films.find { it.titre.equals(title, ignoreCase = true) }?.let {
                    return Movie(it.titre, "", it.genre, it.annee.toString(), fran.nom, saga.nom, "N° ${it.numero}")
                }
            }
        }
    }
    return null
}

@Composable
fun DetailledMovie(movie: Movie, modifier: Modifier = Modifier, apiKey: String) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = movie.imageUrl,
            contentDescription = "Affiche du film ${movie.title}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(200.dp)
                .height(300.dp)
                .padding(top = 16.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        )

        Text(
            text = movie.title,
            fontSize = 40.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            color = colorResource(R.color.white)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            GlassyCardTag(text = movie.genre)
            GlassyCardTag(text = movie.year)
            GlassyCardTag(text = movie.number)
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            GlassyInfoCard(label = "Univers", value = movie.universe)
            GlassyInfoCard(label = "Saga", value = movie.saga)
        }
    }
}