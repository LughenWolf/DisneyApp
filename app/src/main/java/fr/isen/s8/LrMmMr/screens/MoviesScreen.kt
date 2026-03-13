package fr.isen.s8.LrMmMr.screens

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import fr.isen.s8.LrMmMr.R
import fr.isen.s8.LrMmMr.components.BlackGlassyCardTag
import fr.isen.s8.LrMmMr.components.FilmStripCard
import fr.isen.s8.LrMmMr.models.Movies.Movie
import fr.isen.s8.LrMmMr.models.FirebaseCategory
import fr.isen.s8.LrMmMr.network.ApiClient
import fr.isen.s8.LrMmMr.dataClasses.MovieSearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class MovieSortOrder {
    DEFAULT, DATE_ASC, DATE_DESC, UNIVERSE, SAGA, WANT_TO_WATCH, ALREADY_WATCHED
}

fun extractAllMovies(categories: List<FirebaseCategory>): List<Movie> {
    val allMovies = mutableListOf<Movie>()
    for (cat in categories) {
        for (fran in cat.franchises) {
            fran.films?.forEach { film ->
                allMovies.add(
                    Movie(film.titre, "", film.genre, film.annee.toString(), cat.categorie, fran.nom, "N° ${film.numero}")
                )
            }
            fran.sous_sagas?.forEach { saga ->
                saga.films.forEach { film ->
                    allMovies.add(
                        Movie(film.titre, "", film.genre, film.annee.toString(), fran.nom, saga.nom, "N° ${film.numero}")
                    )
                }
            }
        }
    }
    return allMovies
}

class MoviesViewModel : ViewModel() {
    private val _rawMoviesList = MutableStateFlow<List<Movie>>(emptyList())
    val rawMoviesList: StateFlow<List<Movie>> = _rawMoviesList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Le cache est dans le ViewModel pour survivre aux changements d'écrans !
    val imageCache = mutableMapOf<String, String?>()

    private var hasLoadedData = false

    fun loadMoviesIfNeeded() {
        if (hasLoadedData) return

        val database = Firebase.database.reference.child("categories")
        database.get().addOnSuccessListener { snapshot ->
            val categories = snapshot.children.mapNotNull { it.getValue<FirebaseCategory>() }
            _rawMoviesList.value = extractAllMovies(categories).shuffled()
            hasLoadedData = true
            _isLoading.value = false
        }.addOnFailureListener {
            _isLoading.value = false
            Log.e("MoviesViewModel", "Erreur Firebase", it)
        }
    }
}

@Composable
fun AllMoviesScreen(
    apiKey: String,
    onMovieClick: (String) -> Unit
) {
    // 1. On récupère le contexte de l'application
    val context = LocalContext.current

    // 2. On attache le ViewModel à l'Activité Principale, et non plus à cet écran
    val viewModel: MoviesViewModel = viewModel(viewModelStoreOwner = context as ComponentActivity)

    val rawMoviesList by viewModel.rawMoviesList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val imageCache = viewModel.imageCache // On utilise le cache du ViewModel

    var sortOrder by remember { mutableStateOf(MovieSortOrder.DEFAULT) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    var wantToWatchMovies by remember { mutableStateOf<List<String>>(emptyList()) }
    var watchedMovies by remember { mutableStateOf<List<String>>(emptyList()) }

    val gridState = rememberLazyGridState()

    val sortedMovies = remember(rawMoviesList, sortOrder, wantToWatchMovies, watchedMovies) {
        when (sortOrder) {
            MovieSortOrder.DEFAULT -> rawMoviesList
            MovieSortOrder.DATE_ASC -> rawMoviesList.sortedBy { it.year.toIntOrNull() ?: 9999 }
            MovieSortOrder.DATE_DESC -> rawMoviesList.sortedByDescending { it.year.toIntOrNull() ?: 0 }
            MovieSortOrder.UNIVERSE -> rawMoviesList.sortedWith(compareBy<Movie> { it.universe }.thenBy { it.year.toIntOrNull() ?: 9999 })
            MovieSortOrder.SAGA -> rawMoviesList.sortedWith(compareBy<Movie> { it.saga }.thenBy { it.year.toIntOrNull() ?: 9999 })
            MovieSortOrder.WANT_TO_WATCH -> rawMoviesList.filter { it.title in wantToWatchMovies }
            MovieSortOrder.ALREADY_WATCHED -> rawMoviesList.filter { it.title in watchedMovies }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadMoviesIfNeeded()
    }

    DisposableEffect(currentUser?.uid) {
        val uid = currentUser?.uid
        if (uid != null) {
            val userRef = Firebase.database.reference.child("users").child(uid)

            val wantToWatchRef = userRef.child("wantToWatch")
            val wantToWatchListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    wantToWatchMovies = snapshot.children.mapNotNull { it.key }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("AllMoviesScreen", "Erreur wantToWatch", error.toException())
                }
            }
            wantToWatchRef.addValueEventListener(wantToWatchListener)

            val watchedRef = userRef.child("watched")
            val watchedListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    watchedMovies = snapshot.children.mapNotNull { it.key }
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e("AllMoviesScreen", "Erreur watched", error.toException())
                }
            }
            watchedRef.addValueEventListener(watchedListener)

            onDispose {
                wantToWatchRef.removeEventListener(wantToWatchListener)
                watchedRef.removeEventListener(watchedListener)
            }
        } else {
            onDispose { }
        }
    }

    LaunchedEffect(sortOrder) {
        if (sortedMovies.isNotEmpty()) {
            gridState.scrollToItem(0)
        }
    }

    val screenTitle = when (sortOrder) {
        MovieSortOrder.DEFAULT -> "Tous les Films"
        MovieSortOrder.DATE_ASC -> "Plus anciens"
        MovieSortOrder.DATE_DESC -> "Plus récents"
        MovieSortOrder.UNIVERSE -> "Par Univers"
        MovieSortOrder.SAGA -> "Par Catégorie / Saga"
        MovieSortOrder.WANT_TO_WATCH -> "À voir"
        MovieSortOrder.ALREADY_WATCHED -> "Déjà vus"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(listOf(colorResource(R.color.egyptian_blue), colorResource(R.color.glaucous))))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = screenTitle,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                var showMenu by remember { mutableStateOf(false) }
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Sort, contentDescription = "Trier", tint = Color.White)
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Par défaut") },
                            onClick = { sortOrder = MovieSortOrder.DEFAULT; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Par Date (Plus anciens)") },
                            onClick = { sortOrder = MovieSortOrder.DATE_ASC; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Par Date (Plus récents)") },
                            onClick = { sortOrder = MovieSortOrder.DATE_DESC; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Par Univers") },
                            onClick = { sortOrder = MovieSortOrder.UNIVERSE; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Par Catégorie / Saga") },
                            onClick = { sortOrder = MovieSortOrder.SAGA; showMenu = false }
                        )

                        if (currentUser != null) {
                            HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                            DropdownMenuItem(
                                text = { Text("À voir") },
                                onClick = { sortOrder = MovieSortOrder.WANT_TO_WATCH; showMenu = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Déjà vus") },
                                onClick = { sortOrder = MovieSortOrder.ALREADY_WATCHED; showMenu = false }
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else if (sortedMovies.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Aucun film trouvé dans cette liste ",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    state = gridState,
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    if (sortOrder == MovieSortOrder.SAGA || sortOrder == MovieSortOrder.UNIVERSE) {
                        val groupedMovies = if (sortOrder == MovieSortOrder.SAGA) {
                            sortedMovies.groupBy { it.saga }
                        } else {
                            sortedMovies.groupBy { it.universe }
                        }

                        groupedMovies.forEach { (categoryName, moviesInGroup) ->
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                FilmStripCard(
                                    text = categoryName.ifEmpty { "Autre" },
                                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                )
                            }
                            items(
                                items = moviesInGroup,
                                key = { movie -> "${movie.title}_${movie.year}_${movie.saga}" }
                            ) { movie ->
                                MovieGridItem(
                                    movie = movie,
                                    apiKey = apiKey,
                                    imageCache = imageCache,
                                    onClick = { onMovieClick(movie.title) }
                                )
                            }
                        }
                    } else {
                        items(
                            items = sortedMovies,
                            key = { movie -> "${movie.title}_${movie.year}_${movie.saga}" }
                        ) { movie ->
                            MovieGridItem(
                                movie = movie,
                                apiKey = apiKey,
                                imageCache = imageCache,
                                onClick = { onMovieClick(movie.title) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MovieGridItem(movie: Movie, apiKey: String, imageCache: MutableMap<String, String?>, onClick: () -> Unit) {
    var tmdbImageUrl by remember(movie.title) { mutableStateOf(imageCache[movie.title]) }

    DisposableEffect(movie.title) {
        if (imageCache.containsKey(movie.title)) {
            tmdbImageUrl = imageCache[movie.title]
            return@DisposableEffect onDispose { }
        }

        tmdbImageUrl = null
        val call = ApiClient.retrofit.searchMovie(apiKey = apiKey, query = movie.title)

        call.enqueue(object : Callback<MovieSearchResponse> {
            override fun onResponse(call: Call<MovieSearchResponse>, response: Response<MovieSearchResponse>) {
                if (response.isSuccessful) {
                    val path = response.body()?.results?.firstOrNull()?.posterPath
                    val fullUrl = if (path != null) "https://image.tmdb.org/t/p/w500$path" else null

                    tmdbImageUrl = fullUrl
                    imageCache[movie.title] = fullUrl
                } else {
                    imageCache[movie.title] = null
                }
            }
            override fun onFailure(call: Call<MovieSearchResponse>, t: Throwable) {
                if (!call.isCanceled) {
                    tmdbImageUrl = null
                }
            }
        })

        onDispose {
            call.cancel()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.66f)
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray)
        ) {
            AsyncImage(
                model = tmdbImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            if (movie.year.isNotBlank() && movie.year != "null" && movie.year != "0") {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                ) {
                    BlackGlassyCardTag(text = movie.year)
                }
            }
        }

        Text(
            text = movie.title,
            color = Color.White,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            minLines = 2,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 4.dp, start = 2.dp, end = 2.dp)
        )
    }
}