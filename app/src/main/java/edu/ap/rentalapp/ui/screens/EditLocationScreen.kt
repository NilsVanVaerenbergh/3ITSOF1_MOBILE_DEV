package edu.ap.rentalapp.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.rentalapp.components.OSM
import edu.ap.rentalapp.components.findGeoLocationFromAddress
import edu.ap.rentalapp.components.getAddressFromLatLng
import edu.ap.rentalapp.entities.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun EditLocationScreen(
    modifier: Modifier = Modifier,
    context: Context,
    navController: NavController,
    user: User?,
    addressLine: String
) {

    var address by remember { mutableStateOf(addressLine) }
    var latitude by remember { mutableDoubleStateOf(user!!.lat.toDouble()) }
    var longitude by remember { mutableDoubleStateOf(user!!.lon.toDouble()) }

    var isSaving by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Address input field
        TextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Set location to") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                findGeoLocationFromAddress(
                    address = address,
                    context = context,
                    assignLat = { lat ->
                        latitude = lat
                    },
                    assignLon = { lon ->
                        longitude = lon
                    },
                )

            },
            modifier = modifier
                .padding(bottom = 15.dp)
                .fillMaxWidth()
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location icon"
                )
                Text("Find Location")
            }

        }

        Box(
            modifier = modifier
                .aspectRatio(0.75f) // Adjust aspect ratio (e.g., 1:1 for a square map view)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, Color.LightGray)
                .weight(1f)
                .fillMaxWidth()
        ) {
            OSM(
                latitude = latitude,
                longitude = longitude,
                context = context,
                appliances = emptyList(),
                modifier = modifier
                    //.height(400.dp)
                    .padding(15.dp)
                    .fillMaxSize()

            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Submit button
        Button(
            onClick = {
                isSaving = true
                coroutineScope.launch {
                    if (user != null) {
                        updateUserLocation(context, user.userId, latitude, longitude, coroutineScope)
                        navController.popBackStack()
                    } else {
                        Toast.makeText(context, "An error occured while saving", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
                isSaving = false
            },
            enabled = !isSaving
        ) {
            Text(if (isSaving) "Saving..." else "Save Location")
        }
    }

}

fun updateUserLocation(context: Context, userId: String, newLat: Double, newLon: Double, coroutineScope: CoroutineScope) {

    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    val locationUpdate = userRef.update(
        mapOf(
            "lat" to newLat.toString(),
            "lon" to newLon.toString()
        )
    ).addOnSuccessListener {
        Log.d("UpdateLocation", "Location updated successfully!")
    }.addOnFailureListener { e ->
        Log.e("UpdateLocation", "Error updating location", e)
    }

    val addressUpdate = Tasks.whenAllComplete(locationUpdate)
        .addOnSuccessListener {
            coroutineScope.launch {
                val address = getAddressFromLatLng(context, newLat, newLon)
                userRef.update(
                    mapOf(
                        "address" to address
                    )
                ).addOnSuccessListener {
                    Log.d("UpdateAddress", "Address updated successfully!")
                }.addOnFailureListener { e ->
                    Log.e("UpdateAddress", "Error updating address", e)
                }

            }
        }
}