package fr.isen.s8.LrMmMr.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
// Nouveaux imports pour la base de données :
import com.google.firebase.Firebase
import com.google.firebase.database.database
import fr.isen.s8.LrMmMr.R

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    onSuccess: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    // Remplacer par l'ID de ta police si ajoutée dans res/font
    // val disneyFont = FontFamily(Font(R.font.disney_font))
    val disneyStyle = FontFamily.Cursive

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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO CHATEAU ---
            Icon(
                painter = painterResource(id = R.drawable.ic_castle),
                contentDescription = "Disney Castle",
                modifier = Modifier.size(200.dp),
                tint = Color.Unspecified
            )

            Spacer(Modifier.height(8.dp))

            // --- TITRE DISNEY ---
            Text(
                text = if (isRegistering) "Ready to join us ?" else "Welcome Back",
                fontFamily = disneyStyle,
                fontSize = 42.sp,
                color = colorResource(R.color.white)
            )

            Spacer(Modifier.height(32.dp))

            // --- CARD TRANSPARENTE (GLASSY FORM) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.15f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isRegistering) "Register" else "Login",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 2.sp
                    )

                    Spacer(Modifier.height(24.dp))

                    // Champ Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = colorResource(R.color.pale_sky)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.baby_blue_ice),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // Champ Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = colorResource(R.color.pale_sky)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.baby_blue_ice),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Spacer(Modifier.height(32.dp))

                    // Bouton principal
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        onClick = {
                            // Nettoyage de l'e-mail (retire les espaces de fin de saisie)
                            val cleanEmail = email.trim()

                            if (cleanEmail.isNotEmpty() && password.isNotEmpty()) {
                                if (isRegistering) {
                                    auth.createUserWithEmailAndPassword(cleanEmail, password)
                                        .addOnSuccessListener { authResult ->
                                            // --- SAUVEGARDE DE L'EMAIL DANS LA BDD ---
                                            val uid = authResult.user?.uid
                                            if (uid != null) {
                                                Firebase.database.reference
                                                    .child("users")
                                                    .child(uid)
                                                    .child("email")
                                                    .setValue(cleanEmail)
                                                    .addOnSuccessListener {
                                                        onSuccess()
                                                    }
                                            } else {
                                                onSuccess()
                                            }
                                            // ------------------------------------------
                                        }
                                        .addOnFailureListener {
                                            // Affiche le vrai message d'erreur
                                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    auth.signInWithEmailAndPassword(cleanEmail, password)
                                        .addOnSuccessListener { onSuccess() }
                                        .addOnFailureListener { exception ->
                                            // Affiche le vrai message d'erreur
                                            Toast.makeText(context, exception.message ?: "Authentication failed", Toast.LENGTH_LONG).show()
                                        }
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.baby_blue_ice),
                            contentColor = colorResource(R.color.egyptian_blue)
                        )
                    ) {
                        Text(
                            text = if (isRegistering) "CREATE ACCOUNT" else "SIGN IN",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Toggle Login / Register
            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(
                    text = if (isRegistering) "Already a member? Sign In" else "New to the magic? Join us",
                    color = colorResource(R.color.pale_sky),
                    fontSize = 14.sp
                )
            }
        }
    }
}