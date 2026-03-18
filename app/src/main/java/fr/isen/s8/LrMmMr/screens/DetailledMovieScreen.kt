package fr.isen.s8.LrMmMr.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
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
import fr.isen.s8.LrMmMr.dataClasses.VideoResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import fr.isen.s8.LrMmMr.components.GlassyCardTag
import fr.isen.s8.LrMmMr.components.GlassyInfoCard
import fr.isen.s8.LrMmMr.components.CustomTopBar
import fr.isen.s8.LrMmMr.managers.UserMovieManager

data class UserContact(val username: String, val email: String)

@Composable
fun DetailledMovieScreen(
    movieTitle: String,
    apiKey: String,
    isConnected: Boolean,
    userUid: String,
    onBackPressed: () -> Unit
) {
    var movieDetails by remember { mutableStateOf<Movie?>(null) }
    var tmdbImageUrl by remember { mutableStateOf<String?>(null) }
    var trailerUrl by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var isWantToWatch by remember { mutableStateOf(false) }
    var isWatched by remember { mutableStateOf(false) }
    var isOwn by remember { mutableStateOf(false) }
    var isWantToGetRidOf by remember { mutableStateOf(false) }

    var owners by remember { mutableStateOf<List<UserContact>>(emptyList()) }
    var sellers by remember { mutableStateOf<List<UserContact>>(emptyList()) }

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
                            val results = response.body()?.results
                            val targetYear = found.year
                            val result = results?.firstOrNull { tmdbMovie ->
                                tmdbMovie.releaseDate?.startsWith(targetYear) == true
                            } ?: results?.firstOrNull()

                            if (result?.posterPath != null) {
                                tmdbImageUrl = "https://image.tmdb.org/t/p/w500${result.posterPath}"
                            }

                            if (result?.id != null) {
                                ApiClient.retrofit.getMovieVideos(movieId = result.id, apiKey = apiKey)
                                    .enqueue(object : Callback<VideoResponse> {
                                        override fun onResponse(call: Call<VideoResponse>, videoResponse: Response<VideoResponse>) {
                                            val trailer = videoResponse.body()?.results?.firstOrNull {
                                                it.site.equals("YouTube", ignoreCase = true) && it.type.equals("Trailer", ignoreCase = true)
                                            } ?: videoResponse.body()?.results?.firstOrNull {
                                                it.site.equals("YouTube", ignoreCase = true)
                                            }

                                            if (trailer != null) {
                                                trailerUrl = "https://www.youtube.com/watch?v=${trailer.key}"
                                            }
                                            isLoading = false
                                        }

                                        override fun onFailure(call: Call<VideoResponse>, t: Throwable) {
                                            isLoading = false
                                        }
                                    })
                            } else {
                                isLoading = false
                            }
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

        if (isConnected && userUid.isNotEmpty()) {
            val userRef = Firebase.database.reference.child("users").child(userUid)

            userRef.child("wantToWatch").child(movieTitle).get().addOnSuccessListener { isWantToWatch = it.exists() }
            userRef.child("watched").child(movieTitle).get().addOnSuccessListener { isWatched = it.exists() }
            userRef.child("own").child(movieTitle).get().addOnSuccessListener { isOwn = it.exists() }
            userRef.child("wantToGetRidOf").child(movieTitle).get().addOnSuccessListener { isWantToGetRidOf = it.exists() }
        }

        val allUsersRef = Firebase.database.reference.child("users")
        allUsersRef.get().addOnSuccessListener { snapshot ->
            val ownersList = mutableListOf<UserContact>()
            val sellersList = mutableListOf<UserContact>()

            for (userSnapshot in snapshot.children) {
                val ownsMovie = userSnapshot.child("own").child(movieTitle).exists()
                val wantsToGetRidOfMovie = userSnapshot.child("wantToGetRidOf").child(movieTitle).exists()

                if (ownsMovie && userSnapshot.key != userUid) {
                    val email = userSnapshot.child("email").getValue(String::class.java) ?: "Email inconnu"
                    val username = userSnapshot.child("username").getValue(String::class.java)
                        ?: "Membre mystère (ID: ${userSnapshot.key?.take(5)}...)"

                    val contact = UserContact(username, email)

                    if (wantsToGetRidOfMovie) {
                        sellersList.add(contact)
                    } else {
                        ownersList.add(contact)
                    }
                }
            }
            owners = ownersList
            sellers = sellersList
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
                isWantToWatch = isWantToWatch,
                isWatched = isWatched,
                isOwn = isOwn,
                isWantToGetRidOf = isWantToGetRidOf,
                onBackClick = { onBackPressed() },
                onWantToWatchClick = {
                    isWantToWatch = !isWantToWatch
                    UserMovieManager.toggleMovieStatus(userUid, movieTitle, "wantToWatch", isWantToWatch)
                },
                onWatchClick = {
                    isWatched = !isWatched
                    UserMovieManager.toggleMovieStatus(userUid, movieTitle, "watched", isWatched)
                },
                onOwnClick = {
                    isOwn = !isOwn
                    UserMovieManager.toggleMovieStatus(userUid, movieTitle, "own", isOwn)
                    if (!isOwn && isWantToGetRidOf) {
                        isWantToGetRidOf = false
                        UserMovieManager.toggleMovieStatus(userUid, movieTitle, "wantToGetRidOf", false)
                    }
                },
                onDeleteClick = {
                    isWantToGetRidOf = !isWantToGetRidOf
                    UserMovieManager.toggleMovieStatus(userUid, movieTitle, "wantToGetRidOf", isWantToGetRidOf)
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color.White)
                } else if (movieDetails != null) {
                    DetailledMovie(
                        movie = movieDetails!!.copy(imageUrl = tmdbImageUrl ?: ""),
                        trailerUrl = trailerUrl,
                        owners = owners,
                        sellers = sellers
                    )
                } else {
                    Text("Film introuvable", color = Color.White, modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

fun findMovieInCategories(categories: List<FirebaseCategory>, title: String): Movie? {
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
fun DetailledMovie(
    movie: Movie,
    modifier: Modifier = Modifier,
    trailerUrl: String?,
    owners: List<UserContact> = emptyList(),
    sellers: List<UserContact> = emptyList()
) {
    val context = LocalContext.current
    var selectedUser by remember { mutableStateOf<UserContact?>(null) }

    if (selectedUser != null) {
        AlertDialog(
            onDismissRequest = { selectedUser = null },
            containerColor = colorResource(R.color.egyptian_blue),
            titleContentColor = colorResource(R.color.baby_blue_ice),
            textContentColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = "Contact Info",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "PSEUDO",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.pale_sky),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = selectedUser!!.username,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.width(100.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.2f)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "EMAIL",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.pale_sky),
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = selectedUser!!.email,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedUser = null },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.baby_blue_ice),
                        contentColor = colorResource(R.color.egyptian_blue)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Fermer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AsyncImage(
            model = movie.imageUrl.ifEmpty { null },
            contentDescription = "Affiche du film ${movie.title}",
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.no_movies_images),
            fallback = painterResource(id = R.drawable.no_movies_images),
            modifier = Modifier
                .width(200.dp)
                .height(300.dp)
                .padding(top = 16.dp)
                .shadow(8.dp, RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
        )

        if (trailerUrl != null) {
            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.baby_blue_ice)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = colorResource(R.color.egyptian_blue))
                Spacer(Modifier.width(8.dp))
                Text("Voir le Trailer", color = colorResource(R.color.egyptian_blue), fontWeight = FontWeight.Bold)
            }
        }

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

        if (owners.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ils l'ont dans leur collection :",
                color = colorResource(R.color.baby_blue_ice),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            owners.forEach { contact ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedUser = contact }
                ) {
                    GlassyInfoCard(label = "Membre", value = contact.username)
                }
            }
        }

        if (sellers.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ils veulent s'en débarrasser :",
                color = colorResource(R.color.baby_blue_ice),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            sellers.forEach { contact ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { selectedUser = contact }
                ) {
                    GlassyInfoCard(label = "Contact", value = contact.username)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}