package edu.ap.rentalapp.ui.screens

import android.content.Context
import android.location.Geocoder
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import edu.ap.rentalapp.entities.User
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.util.Locale

@Composable
fun UserProfileScreen(
    modifier: Modifier = Modifier,
    context: Context,
    navController: NavController
) {
    val authenticationManager = remember { AuthenticationManager(context) }
    val userService = remember { UserServiceSingleton.getInstance(context) }
    val user = authenticationManager.auth.currentUser
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true) }
    var userData by remember { mutableStateOf<User?>(null) }

    val userId = user?.uid.toString()

    var address by remember { mutableStateOf("Loading...") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)// Ensure proper padding for visibility
    ) {

        if (user == null) {
            Text(
                text = "Geen gebruiker gevonden...",
            )
        } else {
            Log.d("FIRESTORE", "uid:$userId")
            LaunchedEffect(userId, userData) {
                isLoading = true
                userService.getUserByUserId(userId = userId).onEach { result ->
                    if (result.isFailure) {
                        Toast.makeText(context, "Kon geen gegevens ophalen", Toast.LENGTH_LONG)
                            .show()
                        isLoading = false
                    } else if (result.isSuccess) {
                        val document = result.getOrNull()
                        if (document != null && document.exists()) {
                            userData = document.toObject(User::class.java)
                            Log.d("FIRESTORE", "Mapped User: $userData")
                            address = getAddressFromLatLng(
                                context,
                                userData!!.lat.toDouble(),
                                userData!!.lon.toDouble()
                            ).toString()
                            Log.d(
                                "location",
                                "UserProfileScreen: $address, ${userData!!.lat}, ${userData!!.lon}"
                            )
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
                        modifier = modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
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
                        modifier = modifier
                            .fillMaxWidth()
                            .clickable { editUsername(navController, userData!!) },
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
                                text = userData!!.username,
                            )
                        }
                        Text(
                            text = "Klik om te bewerken",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .clickable { editLocation(navController, userData!!) },
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column {
                            Text(
                                text = "Huidige locatie:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = address,
                            )
                        }
                        Text(
                            text = "Klik om te bewerken",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = modifier.align(Alignment.TopEnd)
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
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { backToHome(navController) },
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
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable {
                    authenticationManager.signOut()
                    navController.navigate("signIn") {
                        // So you can't backtrack back to the profile page/ app (which gives you unauthenticated access, user == null!!!)
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                },
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

fun backToHome(navController: NavController) {
    navController.navigate("home")
}

fun editLocation(navController: NavController, user: User) {
    //val userData = Uri.encode(Gson().toJson(user))
    navController.navigate("editLocation")
}

fun editUsername(navController: NavController, user: User) {
    val userData = Uri.encode(Gson().toJson(user))
    navController.navigate("editUserName/${userData}")

}

suspend fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                addresses[0].getAddressLine(0) // Full address
            } else {
                "Address not found"
            }
        } catch (e: Exception) {
            //e.printStackTrace()
            Log.d("location", "getAddressFromLatLng: $e")
            "Error fetching address"
        }
    }
}
