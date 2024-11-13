package edu.ap.rentalapp.screens
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.ui.theme.RentalAppTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import edu.ap.rentalapp.R
import edu.ap.rentalapp.extensions.AuthResponse
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RentalAppTheme {
                SignInScreen(

                )
            }
        }
    }
}

@Composable
fun SignInScreen(
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
                text = "Inloggen",
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
                placeholder = { Text(text="Email")},
                leadingIcon = { Icon(imageVector = Icons.Outlined.Email, contentDescription = "email icon")},
            )
            Spacer(modifier = Modifier.height(9.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {newPassword -> password = newPassword},
                placeholder = { Text(text="Wachtwoord")},
                leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = "lock icon")},
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(9.dp))
            Button(
                onClick = {
                    if(email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.error_empty_input), Toast.LENGTH_LONG)
                    } else {
                        authenticationManager.signInWithEmail(email,password)
                            .onEach { response ->
                                if(response is AuthResponse.Succes) {
                                    Toast.makeText(context, "SUCCES", Toast.LENGTH_LONG).show()
                                }
                                if(response is AuthResponse.Error) {
                                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                }
                            }.launchIn(coroutineScope) }
                    },
            ) {
                Text(text = "login")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Of registreer je nu.",
            )
            Spacer(modifier = Modifier.height(9.dp))
            Button(
                onClick = {
                    context.startActivity(Intent(context, SignUpActivity::class.java))
                },
            ) {
                Text(text = "Registreren")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SignInActivityPreview() {
    RentalAppTheme {
        SignInScreen()
    }
}


