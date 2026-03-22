package fr.isen.s8.LrMmMr.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import coil3.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import fr.isen.s8.LrMmMr.AuthActivity
import fr.isen.s8.LrMmMr.DetailledMovieActivity
import fr.isen.s8.LrMmMr.R
import fr.isen.s8.LrMmMr.components.GlassyCardTag
import fr.isen.s8.LrMmMr.models.AppBarState

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onComposing: (AppBarState) -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(auth.currentUser) }

    var ownedMovies by remember { mutableStateOf<List<String>>(emptyList()) }

    var username by remember { mutableStateOf<String?>(null) }

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            currentUser = auth.currentUser
        }
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            val userRef = Firebase.database.reference
                .child("users")
                .child(currentUser!!.uid)

            userRef.child("own").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val movies = snapshot.children.mapNotNull { it.key }
                    ownedMovies = movies
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProfileScreen", "Erreur lors de la récupération des films", error.toException())
                }
            })

            // NOUVEAU: Récupération du pseudo
            userRef.child("username").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    username = snapshot.getValue(String::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProfileScreen", "Erreur lors de la récupération du pseudo", error.toException())
                }
            })

        } else {
            ownedMovies = emptyList()
            username = null
        }
    }

    LaunchedEffect(Unit) {
        onComposing(AppBarState("My Disney Profile"))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        colorResource(R.color.egyptian_blue),
                        colorResource(R.color.glaucous)
                    )
                )
            )
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (currentUser == null) Arrangement.Center else Arrangement.Top
        ) {
            if (currentUser == null) {
                Text(
                    text = "Unlock the Magic",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sign in to see your collection",
                    color = colorResource(R.color.pale_sky),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        val intent = Intent(context, AuthActivity::class.java)
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    elevation = null,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    GlassyCardTag("Login / Register")
                }
            } else {
                ProfileContent(
                    userName = username ?: currentUser?.email ?: "Disney Fan",
                    ownedMovies = ownedMovies,
                    onRemoveMovie = { movieTitle ->
                        Firebase.database.reference
                            .child("users")
                            .child(currentUser!!.uid)
                            .child("own")
                            .child(movieTitle)
                            .removeValue()
                    },
                    onMovieClick = { movieTitle ->
                        val intent = Intent(context, DetailledMovieActivity::class.java).apply {
                            putExtra("MOVIE_TITLE", movieTitle)
                        }
                        context.startActivity(intent)
                    },
                    onLogout = {
                        auth.signOut()
                        currentUser = null
                        username = null
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileContent(
    userName: String, 
    ownedMovies: List<String>,
    onRemoveMovie: (String) -> Unit,
    onMovieClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .size(130.dp)
                .border(2.dp, colorResource(R.color.baby_blue_ice), CircleShape)
                .padding(5.dp)
        ) {
            AsyncImage(
                model = R.drawable.stitch,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userName,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = onLogout) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = colorResource(R.color.pale_sky))
                Spacer(Modifier.width(8.dp))
                Text("Déconnexion", color = colorResource(R.color.pale_sky))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.12f)
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "MA COLLECTION",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    letterSpacing = 2.sp,
                    color = colorResource(R.color.baby_blue_ice)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp,
                    color = Color.White.copy(alpha = 0.2f)
                )

                if (ownedMovies.isEmpty()) {
                    Text(
                        text = "Votre collection est vide. Allez ajouter des paillettes dans votre vie! ✨",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                } else {
                    ownedMovies.forEach { movie ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onMovieClick(movie) }
                                    .padding(vertical = 4.dp, horizontal = 4.dp)
                            ) {
                                Text("✨", fontSize = 12.sp)
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = movie,
                                    color = Color.White,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            IconButton(
                                onClick = { onRemoveMovie(movie) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Retirer de la collection",
                                    tint = colorResource(R.color.pale_sky)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}