package fr.isen.s8.LrMmMr.screens

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
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

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

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
            verticalArrangement = Arrangement.Top
        ) {
            Box(modifier = Modifier.padding(top = 40.dp))

            Icon(
                painter = painterResource(id = R.drawable.ic_castle),
                contentDescription = "Disney Castle",
                modifier = Modifier.size(160.dp),
                tint = Color.Unspecified
            )

            Text(
                text = if (isRegistering) "Prêt à nous rejoindre ?" else "Bon retour parmi nous!",
                fontFamily = disneyStyle,
                fontSize = 38.sp,
                color = colorResource(R.color.white),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
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
                        text = if (isRegistering) "S'ENREGISTRER" else "SE CONNECTER",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    if (isRegistering) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it },
                            label = { Text("Nom d'utilisateur", color = colorResource(R.color.pale_sky)) },
                            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.baby_blue_ice),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Adresse mail", color = colorResource(R.color.pale_sky)) },
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.baby_blue_ice),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    } else {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Adresse mail", color = colorResource(R.color.pale_sky)) },
                            modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorResource(R.color.baby_blue_ice),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe", color = colorResource(R.color.pale_sky)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorResource(R.color.baby_blue_ice),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp)
                            .height(56.dp),
                        onClick = {
                            val cleanEmail = email.trim()
                            val cleanUsername = username.trim()

                            if (isRegistering) {
                                if (cleanEmail.isNotEmpty() && password.isNotEmpty() && cleanUsername.isNotEmpty()) {
                                    auth.createUserWithEmailAndPassword(cleanEmail, password)
                                        .addOnSuccessListener { authResult ->
                                            val uid = authResult.user?.uid
                                            if (uid != null) {
                                                val userRef = Firebase.database.reference
                                                    .child("users")
                                                    .child(uid)

                                                userRef.child("email").setValue(cleanEmail)
                                                userRef.child("username").setValue(cleanUsername)
                                                    .addOnSuccessListener {
                                                        onSuccess()
                                                    }
                                                    .addOnFailureListener {
                                                        Toast.makeText(context, "Erreur BDD: ${it.message}", Toast.LENGTH_LONG).show()
                                                    }
                                            } else {
                                                onSuccess()
                                            }
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    Toast.makeText(context, "Remplissez tous les champs", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                if (cleanEmail.isNotEmpty() && password.isNotEmpty()) {
                                    auth.signInWithEmailAndPassword(cleanEmail, password)
                                        .addOnSuccessListener { onSuccess() }
                                        .addOnFailureListener { exception ->
                                            Toast.makeText(context, exception.message ?: "Authentification échouée", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    Toast.makeText(context, "Entrez un mail et un mot de passe", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.baby_blue_ice),
                            contentColor = colorResource(R.color.egyptian_blue)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text(
                            text = if (isRegistering) "Créer un compte" else "Se connecter",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            TextButton(
                onClick = {
                    isRegistering = !isRegistering
                    if (!isRegistering) username = ""
                },
                modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
            ) {
                Text(
                    text = if (isRegistering) "Déjà membre ? Connectez-vous" else "Nouveau ici ? Rejoignez-nous",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
            }
        }
    }
}
