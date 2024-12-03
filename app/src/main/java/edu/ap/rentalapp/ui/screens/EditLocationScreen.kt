package edu.ap.rentalapp.ui.screens

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import edu.ap.rentalapp.extensions.instances.UserServiceSingleton
import edu.ap.rentalapp.ui.SharedTopAppBar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException

@Composable
fun EditLocationScreen(
    modifier: Modifier = Modifier,
    context: Context,
    navController: NavController,
) {

    var address by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column {
        SharedTopAppBar(
            title = "Wijzig locatie",
            navController = navController
        )
        Column(
            modifier = modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Address input field
            TextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Locatie zetten op address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit button
            Button(
                onClick = {
                    coroutineScope.launch {
                        fetchCoordinatesAndSave(address, context)
                    }
                }
            ) {
                Text("Save Location")
            }
        }
    }
}

fun fetchCoordinatesFlow(address: String): Flow<Location?> {
    val client = OkHttpClient()
    val formattedAddress = address.replace(" ", "+") // Format address for URL
    val url = "http://nominatim.openstreetmap.org/search?q=$formattedAddress&format=json&polygon=1&addressdetails=1"
    val request = Request.Builder().url(url).build()

    return flow {
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (!responseBody.isNullOrEmpty()) {
                    val jsonArray = JSONArray(responseBody)
                    if (jsonArray.length() > 0) {
                        val firstResult = jsonArray.getJSONObject(0)
                        val lat = firstResult.getDouble("lat")
                        val lon = firstResult.getDouble("lon")

                        // Create Android Location instance
                        val androidLocation = Location("GeocodingService").apply {
                            latitude = lat
                            longitude = lon
                        }
                        emit(androidLocation)
                        return@flow
                    }
                }
            }
            emit(null) // Emit null if no successful response or data is found
        } catch (e: IOException) {
            e.printStackTrace()
            emit(null) // Emit null on exception
        }
    }
}

suspend fun fetchCoordinatesAndSave(address: String, context: Context) {
    val userService = UserServiceSingleton.getInstance(context)
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid.toString()
    val email = auth.currentUser?.email.toString()

    // Collect the flow from fetchCoordinatesFlow
    fetchCoordinatesFlow(address).collect { location ->
        if (location != null && (userId != null && email != null)) {
            // Save the location to the user service
            val resultFlow = userService.saveUserData(userId, email, location = location)

            // Collect result from the user service flow
            resultFlow.collect { result ->
                if (result.isSuccess) {
                    Toast.makeText(context, "Location saved successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error saving location: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Could not fetch coordinates", Toast.LENGTH_SHORT).show()
        }
    }
}