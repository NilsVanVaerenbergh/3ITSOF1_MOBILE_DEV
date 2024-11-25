package edu.ap.rentalapp.components

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.compose.ui.res.painterResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import edu.ap.rentalapp.MainActivity
import edu.ap.rentalapp.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.OverlayItem
import java.util.*

class MapComponent(context: Context, private val onLocationChanged: (GeoPoint) -> Unit = {}) {

    private val mapView: MapView = MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK) // OpenStreetMap
        setMultiTouchControls(true)
    }

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    init {
        Configuration.getInstance().userAgentValue = context.packageName
        checkPermissions(context)
        initializeLocationListener(context)
    }

    // Location Listener to update user location
    @SuppressLint("MissingPermission")
    private fun initializeLocationListener(context: Context) {
        val locationListener = object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                val userLocation = GeoPoint(loc.latitude, loc.longitude)
                mapView.controller.setCenter(userLocation)
                mapView.invalidate()
                onLocationChanged(userLocation)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10f, locationListener)
        }
    }

    // Permissions check for location
    private fun checkPermissions(context: Context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }
    fun getMapView(): MapView = mapView

    fun setCenter(geoPoint: GeoPoint) {
        mapView.controller.setCenter(geoPoint)
        mapView.invalidate()
    }
    fun setZoom(level: Double) {
        mapView.controller.setZoom(level)
        mapView.invalidate()
    }
}

