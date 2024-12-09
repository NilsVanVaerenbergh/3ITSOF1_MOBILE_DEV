package edu.ap.rentalapp.components

import android.content.Context
import android.graphics.Color
import android.location.Geocoder
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import edu.ap.rentalapp.R
import edu.ap.rentalapp.entities.ApplianceDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import java.io.File
import java.util.Locale
import kotlin.math.cos

@Composable
fun OSM(
    modifier: Modifier = Modifier,
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    zoomLevel: Double = 18.0,
    radius: Double = 0.0,
    appliances: List<ApplianceDTO>,
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
            marker.icon = ContextCompat.getDrawable(context, R.drawable.home2)
            mapView.overlays.add(marker)

            updateMapWithOverlays(
                context,
                mapView,
                GeoPoint(latitude, longitude),
                radius,
                appliances
            )

            // Adjust zoom controls position
            // TODO

            mapView
        },
        update = { mapView ->
            mapView.overlays.removeIf { it is Marker }
            mapView.overlays.removeIf { it is Polygon }

            updateMapWithOverlays(
                context,
                mapView,
                GeoPoint(latitude, longitude),
                radius,
                appliances
            )


            // Update map state if required
            mapView.controller.setCenter(GeoPoint(latitude, longitude))
            mapView.controller.setZoom(zoomLevel)

            val marker = Marker(mapView)
            marker.position = GeoPoint(latitude, longitude)
            marker.title = "Marker at $latitude, $longitude"
            marker.icon = ContextCompat.getDrawable(context, R.drawable.home2)
            mapView.overlays.add(marker)

            //Refresh map
            mapView.invalidate()
        }
    )
}

fun createRadiusCircle(center: GeoPoint, radius: Double, mapView: MapView): Polygon {
    val circle = Polygon(mapView)
    circle.points = Polygon.pointsAsCircle(center, radius * 1000.0) // Radius in meters
    circle.fillPaint.color =
        0x20FF0000 // Transparent red // Color.argb(50, 0, 0, 255) // Transparent blue
    circle.outlinePaint.color = Color.RED
    circle.fillPaint.strokeWidth = 2f
    return circle
}

fun calculateBoundingBox(center: GeoPoint, radiusInMeters: Double): BoundingBox {
    val latRadius =
        radiusInMeters / 111000.0 // Convert radius to latitude degrees (~111 km per degree)
    val lonRadius =
        radiusInMeters / (111000.0 * cos(Math.toRadians(center.latitude))) // Adjust for latitude

    val north = center.latitude + latRadius
    val south = center.latitude - latRadius
    val east = center.longitude + lonRadius
    val west = center.longitude - lonRadius

    return BoundingBox(north, east, south, west)
}

fun updateApplianceMarkers(context: Context, mapView: MapView, appliances: List<ApplianceDTO>) {
    mapView.overlays.removeIf { it is Marker } // Clear previous markers

    appliances.forEach { appliance ->
        val marker = Marker(mapView).apply {
            position = GeoPoint(appliance.latitude, appliance.longitude)
            title = "${appliance.name}\n${appliance.address}"
            when (appliance.category) {
                "Kitchen" -> icon = ContextCompat.getDrawable(context, R.drawable.kitchen)
                "Garden" -> icon = ContextCompat.getDrawable(context, R.drawable.garden)
                "Maintenance" -> icon = ContextCompat.getDrawable(context, R.drawable.maintenance)
                "Other" -> icon = ContextCompat.getDrawable(context, R.drawable.other)
            }
        }
        mapView.overlays.add(marker)
    }
}

fun updateMapWithOverlays(
    context: Context,
    mapView: MapView,
    center: GeoPoint,
    radius: Double,
    appliances: List<ApplianceDTO>
) {

    if (appliances.isNotEmpty() && radius > 0) {
        // Remove existing circles
        mapView.overlays.removeIf { it is Polygon }

        // Add radius circle
        val circle = createRadiusCircle(center, radius, mapView)
        mapView.overlays.add(circle)

        // Add markers for appliances
        updateApplianceMarkers(context, mapView, appliances)


        // Calculate and apply bounding box to fit the circle
        val boundingBox = calculateBoundingBox(center, radius * 1000.0) // Radius in meters
        mapView.zoomToBoundingBox(boundingBox, true)

        // Refresh map
        //mapView.invalidate()
    }
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