package edu.ap.rentalapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.ap.rentalapp.R
import edu.ap.rentalapp.extensions.AuthResponse
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.ui.theme.Green
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    onSignUpSuccess: () -> Unit,
    navController: NavController
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current
    val authenticationManager = remember { AuthenticationManager(context) }
    val coroutineScope = rememberCoroutineScope()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.LightGray
    ) {
        Column (
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Sign up",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Welcome to RentalApp",
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
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(9.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {newPassword -> password = newPassword},
                placeholder = { Text(text="Password") },
                leadingIcon = { Icon(imageVector = Icons.Outlined.Lock, contentDescription = "lock icon") },
                visualTransformation = PasswordVisualTransformation()
            )
            Spacer(modifier = Modifier.height(9.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = Color.White
                ),
                onClick = {
                    if(email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, context.getString(R.string.error_empty_input), Toast.LENGTH_LONG).show()
                    } else {
                        authenticationManager.signUpWithEmail(email,password)
                            .onEach { response ->
                                if(response is AuthResponse.Success) {
                                    onSignUpSuccess()
                                }
                                if(response is AuthResponse.Error) {
                                    Toast.makeText(context, response.message, Toast.LENGTH_LONG).show()
                                }
                            }.launchIn(coroutineScope) }
                },
            ) {
                Text(text = "Sign up")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Already have an account? Sign in here.",
            )
            Spacer(modifier = Modifier.height(9.dp))
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = Color.White
                ),
                onClick = {
                    navController.navigate("signIn")
                },
            ) {
                Text(text = "Sign in")
            }

        }
    }
    
}