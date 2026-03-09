package fr.isen.s8.LrMmMr.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import fr.isen.s8.LrMmMr.AuthActivity
import fr.isen.s8.LrMmMr.models.AppBarState

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onComposing: (AppBarState) -> Unit
) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    var currentUser by remember { mutableStateOf(auth.currentUser) }

    LaunchedEffect(Unit) {
        onComposing(AppBarState("Profile"))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (currentUser == null) Arrangement.Center else Arrangement.Top
    ) {
        if (currentUser == null) {
            Text(
                text = "Sorry, we don't know you (yet)",
                fontSize = 18.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                val intent = Intent(context, AuthActivity::class.java)
                context.startActivity(intent)
            }) {
                Text("Login / Register")
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

@Composable
fun ProfileContent(userEmail: String, onLogout: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        AsyncImage(
            model = "https://img.daisyui.com/images/stock/photo-1534528741775-53994a69daeb.webp",
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = userEmail, fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            TextButton(onClick = { /* Action Settings */ }) {
                Icon(Icons.Default.Settings, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Settings")
            }

            TextButton(onClick = onLogout) {
                Text("Logout", color = MaterialTheme.colorScheme.error)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Owned Movies", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                listOf("The Lion King", "Frozen", "Aladdin", "Mulan").forEach { movie ->
                    Text(text = "• $movie", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}