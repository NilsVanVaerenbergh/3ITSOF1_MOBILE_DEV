package edu.ap.rentalapp.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.ap.rentalapp.MainActivity
import edu.ap.rentalapp.R
import edu.ap.rentalapp.extensions.AuthResponse
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.ui.theme.RentalAppTheme
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RentalAppTheme {
                SignUpScreen()
            }
        }
    }
}

@Composable
fun SignUpScreen(
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") } // Loading state to prevent multiple sign-in attempts

    val context = LocalContext.current
    val authenticationManager = remember { AuthenticationManager(context) }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5) // Light gray background
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Registreren",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Welkom bij RentalApp",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {newEmail -> email = newEmail},
                placeholder = { Text(text="Email") },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = "email icon") },
            )
            Spacer(modifier = Modifier.height(9.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {newPassword -> password = newPassword},
                placeholder = { Text(text="Wachtwoord") },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = "lock icon") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(9.dp))
            Button(
                onClick = {
                    if(email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.error_empty_input), Toast.LENGTH_LONG)
                    } else {
                        authenticationManager.signUpWithEmail(email,password)
                            .onEach { response ->
                                if(response is AuthResponse.Succes) {
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                }
                                if(response is AuthResponse.Error) {
                                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                }
                            }.launchIn(coroutineScope) }
                },
            ) {
                Text(text = "Registreer")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Heb je al een account? Log je nu in.",
            )
            Spacer(modifier = Modifier.height(9.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, SignInActivity::class.java))
                },
            ) {
                Text(text = "Inloggen")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInActivityPreview() {
    RentalAppTheme {
        SignUpScreen()
    }
}
