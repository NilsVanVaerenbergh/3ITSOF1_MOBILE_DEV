package edu.ap.rentalapp.ui.screens

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import edu.ap.rentalapp.components.getAddressFromLatLng
import edu.ap.rentalapp.entities.User
import edu.ap.rentalapp.extensions.AuthenticationManager
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
        modifier = modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)// Ensure proper padding for visibility
    ) {

        if (user == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth() // Ensures the Column takes up the full width
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center, // Centers content vertically
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.SentimentVeryDissatisfied,
                    contentDescription = "Sad",
                    tint = Color.Gray.copy(0.6f)
                )
                Text(
                    text = "Nothing user found",
                    modifier = modifier
                        .padding(20.dp)
                        .align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray.copy(0.6f)
                )
            }
        } else {
            Log.d("FIRESTORE", "uid:$userId")
            LaunchedEffect(userId, userData) {
                isLoading = true
                userService.getUserByUserId(userId = userId).onEach { result ->
                    if (result.isFailure) {
                        Toast.makeText(context, "Failed to fetch user", Toast.LENGTH_LONG)
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
                text = "Your profile:",
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
                                text = "Username: ",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = userData!!.username,
                            )
                        }
                        Text(
                            text = "Click to edit",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .clickable { editLocation(navController, userData!!, address) },
                        contentAlignment = Alignment.TopStart
                    ) {
                        Column {
                            Text(
                                text = "Current loaction:",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            Text(
                                text = address,
                            )
                        }
                        Text(
                            text = "Click to edit",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = modifier.align(Alignment.TopEnd)
                        )
                    }
                }

                else -> {
                    // Display error or empty state
                    Text("No user data found.", color = Color.Red)
                }
            }
        }
        Column {
            Text(
                text = "User actions:",
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
            Text("Back to home")
            Text(
                text = "Click to launch",
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
            Text("Log out", color = Color.Red)
            Text(
                text = "Click to launch",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

fun backToHome(navController: NavController) {
    navController.navigate("home")
}

fun editLocation(navController: NavController, user: User, address: String) {
    val userData = Uri.encode(Gson().toJson(user))
    navController.navigate("editLocation/${userData}/${address}")
}

fun editUsername(navController: NavController, user: User) {
    val userData = Uri.encode(Gson().toJson(user))
    navController.navigate("editUserName/${userData}")

}
