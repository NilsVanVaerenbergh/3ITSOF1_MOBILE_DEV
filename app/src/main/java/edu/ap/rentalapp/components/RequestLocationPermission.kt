package edu.ap.rentalapp.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import edu.ap.rentalapp.extensions.AuthenticationManager

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestLocationPermission(onPermissionGranted: () -> Unit) {
    val context = LocalContext.current
    val activity = context as? Activity

    val permissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        permissionState.launchPermissionRequest()
    }

    if (permissionState.status.isGranted) {
        onPermissionGranted()
    } else if (permissionState.status.shouldShowRationale) {
        // Show rationale UI if needed
        Text("Location permission is needed to continue.")
    } else {
        Button(onClick = { activity?.finish() }) {
            Text("Permission Denied. Exit.")
        }
    }
}

fun getCurrentLocation(context: Context, onLocationResult: (Location?) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    fusedLocationClient.lastLocation
        .addOnSuccessListener { location: Location? ->
            onLocationResult(location)
        }
        .addOnFailureListener {
            onLocationResult(null)
        }
}

fun saveUserLocationToFirebase(context: Context, latitude: Double, longitude: Double) {
    val userId = AuthenticationManager(context).getCurrentUser()?.uid
    val userRef = FirebaseFirestore.getInstance().collection("users").document(userId!!)
    userRef.update(
        mapOf(
            "lat" to latitude.toString(),
            "lon" to longitude.toString()
        )
    ).addOnSuccessListener {
        Log.d("Firebase", "Location updated successfully.")
    }.addOnFailureListener { exception ->
        Log.e("Firebase", "Error updating location: ${exception.message}")
    }
}