package edu.ap.rentalapp.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import edu.ap.rentalapp.MainActivity
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton
import edu.ap.rentalapp.middleware.AuthActivity

class UserProfileActivity : AuthActivity() {
    override fun getTopBarTitle(): String = "Jouw profiel"

    @Composable
    override fun ScreenContent(modifier: Modifier, context: Context) {
        val authenticationManager = remember { AuthenticationManager(context) }
        val userService = remember { UserServiceSingleton.getInstance(context) }
        val user = authenticationManager.auth.currentUser
        Column (modifier = modifier.fillMaxSize().padding(16.dp), // Ensure proper padding for visibility
            verticalArrangement = Arrangement.spacedBy(3.dp)) {

            if(user == null) {
                Text(
                    text = "Geen gebruiker gegevens gevonden...",
                )
            } else {
                Text(
                    text = "Jouw gegevens:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )
                Row (
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Text(
                        text = "Email: ${user.email}",
                    )
                }
            }
            Text(
                text = "Gebruikeracties:",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
            )
            Button(
                onClick = {backToHome()}
            ) { Text("Terug naar startpagina") }
            Button(
                onClick = {authenticationManager.signOut()}
            ) { Text("Log uit.") }
        }
    }
    fun backToHome() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
}

