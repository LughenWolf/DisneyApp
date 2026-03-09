package fr.isen.s8.LrMmMr.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        colorResource(R.color.egyptian_blue),
                        colorResource(R.color.claucous)
                    )
                )
            )
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = if (isRegistering) "Create Account" else "Sign In",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = colorResource(R.color.white)
            )

            Spacer(Modifier.height(40.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = colorResource(R.color.baby_blue_ice)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.baby_blue_ice),
                    unfocusedBorderColor = colorResource(R.color.claucous),
                    focusedTextColor = colorResource(R.color.white),
                    unfocusedTextColor = colorResource(R.color.white)
                )
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = colorResource(R.color.baby_blue_ice)) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colorResource(R.color.baby_blue_ice),
                    unfocusedBorderColor = colorResource(R.color.claucous),
                    focusedTextColor = colorResource(R.color.white),
                    unfocusedTextColor = colorResource(R.color.white)
                )
            )

            Spacer(Modifier.height(32.dp))

            Button(
                modifier = Modifier.fillMaxWidth().height(50.dp),
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        if (isRegistering) {
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() }
                        } else {
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnSuccessListener { onSuccess() }
                                .addOnFailureListener { Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show() }
                        }
                    }
                }
            ) {
                Text(if (isRegistering) "REGISTER" else "LOGIN")
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(
                    text = if (isRegistering) "Already a member? Login" else "New here? Create account",
                    color = colorResource(R.color.baby_blue_ice),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}