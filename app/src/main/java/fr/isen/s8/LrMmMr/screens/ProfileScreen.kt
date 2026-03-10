package fr.isen.s8.LrMmMr.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
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
import com.google.firebase.auth.FirebaseAuth
import fr.isen.s8.LrMmMr.AuthActivity
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

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateAsState()

    LaunchedEffect(lifecycleState) {
        if (lifecycleState == Lifecycle.State.RESUMED) {
            currentUser = auth.currentUser
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
                    userEmail = currentUser?.email ?: "Disney Fan",
                    onLogout = {
                        auth.signOut()
                        currentUser = null
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileContent(userEmail: String, onLogout: () -> Unit) {
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
                model = "https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp",
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = userEmail,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            TextButton(onClick = { /* Settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = colorResource(R.color.baby_blue_ice))
                Spacer(Modifier.width(8.dp))
                Text("Settings", color = colorResource(R.color.baby_blue_ice))
            }

            Spacer(Modifier.width(16.dp))

            TextButton(onClick = onLogout) {
                Icon(Icons.Filled.Logout, contentDescription = null, tint = colorResource(R.color.pale_sky))
                Spacer(Modifier.width(8.dp))
                Text("Logout", color = colorResource(R.color.pale_sky))
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
                    text = "MY COLLECTION",
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

                val movies = listOf("The Lion King", "Frozen", "Aladdin", "Mulan")
                movies.forEach { movie ->
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
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
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}