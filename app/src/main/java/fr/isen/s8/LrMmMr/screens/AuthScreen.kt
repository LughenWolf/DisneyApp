package fr.isen.s8.LrMmMr.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AuthScreen(onSuccess: () -> Unit) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isRegistering) "Create Account" else "Welcome Back", fontSize = 28.sp)

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
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
            Text(if (isRegistering) "Sign Up" else "Login")
        }

        TextButton(onClick = { isRegistering = !isRegistering }) {
            Text(if (isRegistering) "Already have an account? Login" else "No account? Register now")
        }
    }
}