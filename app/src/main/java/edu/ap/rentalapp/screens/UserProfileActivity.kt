package edu.ap.rentalapp.screens

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.DocumentSnapshot
import edu.ap.rentalapp.MainActivity
import edu.ap.rentalapp.entities.User
import edu.ap.rentalapp.extensions.AuthResponse
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton
import edu.ap.rentalapp.middleware.AuthActivity
import edu.ap.rentalapp.ui.theme.RentalAppTheme
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class UserProfileActivity : AuthActivity() {
    override fun getTopBarTitle(): String = "Jouw profiel"

    @Composable
    override fun ScreenContent(modifier: Modifier, context: Context) {
        val authenticationManager = remember { AuthenticationManager(context) }
        val userService = remember { UserServiceSingleton.getInstance(context) }
        val user = authenticationManager.auth.currentUser
        val coroutineScope = rememberCoroutineScope()
        var isLoading by remember { mutableStateOf(true) }
        var userData by remember { mutableStateOf<User?>(null) }

        val userId = user?.uid.toString()
        Column (modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)// Ensure proper padding for visibility
            ) {

            if(user == null) {
                Text(
                    text = "Geen gebruiker gevonden...",
                )
            } else {
                Log.d("FIRESTORE", "uid:" + userId)
                LaunchedEffect(userId) {
                    isLoading = true
                    userService.getUserByUserId(userId = userId).onEach { result ->
                        if (result.isFailure) {
                            Toast.makeText(context, "Kon geen gegevens ophalen", Toast.LENGTH_LONG).show()
                            isLoading = false
                        } else if (result.isSuccess) {
                            val document = result.getOrNull()
                            if (document != null && document.exists()) {
                                userData = document.toObject(User::class.java)
                                Log.d("FIRESTORE", "Mapped User: $userData")
                            } else {
                                Toast.makeText(context, "User not found", Toast.LENGTH_LONG).show()
                            }
                            isLoading = false
                        }
                    }.catch { exception ->
                        // Handle failure
                        Toast.makeText(context, "Error: ${exception.message}", Toast.LENGTH_LONG).show()
                        isLoading = false
                    }.launchIn(coroutineScope)
                }
                Text(
                    text = "Jouw gegevens:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )
                when {
                    isLoading -> {
                        // Display a loading indicator
                        CircularProgressIndicator()
                    }
                    userData != null -> {
                        // Display user data UI
                        Row(
                            modifier = modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom

                        ) {
                            Column {
                                Text(
                                    text = "E-mail:",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    text = user.email.toString(),
                                )
                            }
                        }
                        Row(
                            modifier = modifier.fillMaxWidth().clickable { editUsername(userData!!) },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom

                        ) {
                            Column {
                                Text(
                                    text = "Gebruikersnaam: ",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    text = userData!!.username.toString(),
                                )
                            }
                            Text(
                                text = "Klik om te bewerken",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = modifier.fillMaxWidth().clickable { editLocation(userData!!) },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Huidige locatie:",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                Text(
                                    text = "example street 123 Antwerpen 2000 ",
                                )
                            }
                            Text(
                                text = "Klik om te bewerken",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    else -> {
                        // Display error or empty state
                        Text("Geen gegevens gevonden.", color = Color.Red)
                    }
                }
            }
            Column {
                Text(
                    text = "Gebruikeracties:",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row (
                modifier = modifier.fillMaxWidth().clickable { backToHome() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom

            ) {
                Text("Terug naar start")
                Text(
                    text = "Klik om uit te voeren",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row (
                modifier = modifier.fillMaxWidth().clickable { authenticationManager.signOut() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom

            ) {
                Text("Log uit.", color = Color.Red)
                Text(
                    text = "Klik om uit te voeren",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
    fun backToHome() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        startActivity(intent)
    }
    fun editLocation(user: User) {
        val intent = Intent(this, EditLocationActivity::class.java).apply {
            putExtra("user_data", user) }
        startActivity(intent)
    }
    fun editUsername(user: User) {
        val intent = Intent(this, EditUserNameActivity::class.java).apply {
            putExtra("user_data", user) }
        startActivity(intent)
    }

    @Preview(showBackground = true)
    @Composable
    private fun UserProfilePreview() {
        RentalAppTheme {
            ScreenContent(Modifier.padding(16.dp), LocalContext.current)
        }
    }
}

