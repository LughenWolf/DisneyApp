package fr.isen.s8.LrMmMr.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import fr.isen.s8.LrMmMr.models.AppBarState

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onComposing: (AppBarState) -> Unit
) {
    var isLoggedIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        onComposing(AppBarState("Profile"))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!isLoggedIn) {
            Text(
                text = "Sorry, we don't know you (yet)",
                fontSize = 18.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { isLoggedIn = true }) {
                Text("Login")
            }
        } else {
            ProfileContent()
        }
    }
}

@Composable
fun ProfileContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = "https://via.placeholder.com/150",
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Mickey Mouse", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        TextButton(onClick = { /* Action Settings */ }) {
            Icon(Icons.Default.Settings, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Settings")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Owned Movies", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                listOf("The Lion King", "Frozen", "Aladdin").forEach { movie ->
                    Text(text = "• $movie", modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}