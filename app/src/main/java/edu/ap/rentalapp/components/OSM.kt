package edu.ap.rentalapp.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

@Composable
fun OSM(
    modifier: Modifier = Modifier,
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    zoomLevel: Double = 15.0,
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
            mapView.overlays.clear()

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