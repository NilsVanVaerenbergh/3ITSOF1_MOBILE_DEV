package edu.ap.rentalapp.components

import android.content.Context
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.File
import java.util.Locale

@Composable
fun OSM(
    modifier: Modifier = Modifier,
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    zoomLevel: Double = 18.0,
    context: Context,
) {
    AndroidView(
        modifier = modifier,
        factory = {
            // Initialize OSM configuration
            val config = Configuration.getInstance()
            config.userAgentValue = context.packageName
            config.osmdroidBasePath = File(context.getExternalFilesDir(null), "osmdroid")

            // Initialize MapView
            val mapView = MapView(context)
            mapView.setMultiTouchControls(true) // Enable touch gestures
            mapView.controller.setZoom(zoomLevel)
            mapView.controller.setCenter(GeoPoint(latitude, longitude))

            // Add rotation gesture overlay
            val rotationGestureOverlay = RotationGestureOverlay(mapView)
            rotationGestureOverlay.isEnabled = true
            mapView.overlays.add(rotationGestureOverlay)
            mapView.mapOrientation = 0.0f

            // Add Marker
            val marker = Marker(mapView)
            marker.position = GeoPoint(latitude, longitude)
            marker.title = "Marker at $latitude, $longitude"
            mapView.overlays.add(marker)

            // Adjust zoom controls position
            // TODO

            mapView
        },
        update = { mapView ->
            mapView.overlays.removeIf { it is Marker }

            // Update map state if required
            mapView.controller.setCenter(GeoPoint(latitude, longitude))
            mapView.controller.setZoom(zoomLevel)

            val marker = Marker(mapView)
            marker.position = GeoPoint(latitude, longitude)
            marker.title = "Marker at $latitude, $longitude"
            mapView.overlays.add(marker)

            //Refresh map
            mapView.invalidate()
        }
    )
}

fun findGeoLocationFromAddress(
    address: String,
    assignLat: (latitude: Double) -> Unit,
    assignLon: (longitude: Double) -> Unit,
    context: Context
) {
    if (address.isNotBlank()) {
        val geocoder = Geocoder(context, Locale.getDefault())
        try {
            val results = geocoder.getFromLocationName(address, 1)
            if (results != null) {
                if (results.isNotEmpty()) {
                    val location = results[0]
                    assignLat(location.latitude)
                    assignLon(location.longitude)
                } else {
                    Toast.makeText(
                        context,
                        "Location not found",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error: ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
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