package com.example.uday.diaster_management

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.uday.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class dmap : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dmap)

        // Initialize map view
        mapView = findViewById(R.id.mapView)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Load the map style
        mapView.getMapboxMap().loadStyleUri(Style.SATELLITE_STREETS) { style ->
            // Set initial camera position
            mapView.getMapboxMap().setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(83.0928118, 19.1797914))
                    .zoom(13.0)
                    .build()
            )
                // Add the red marker icon to the map style
                val redMarkerDrawable = getDrawable(R.drawable.red_marker) // Ensure your drawable exists
                redMarkerDrawable?.let { drawable ->
                    val bitmap = BitmapUtils.getBitmapFromDrawable(drawable)
                    bitmap?.let {
                        style.addImage("red_marker", it)
                    }
                }
        }

        // Handle "My Location" button click
        val myLocationButton: FloatingActionButton = findViewById(R.id.mylocation)
        myLocationButton.setOnClickListener {
            fetchAndCenterOnCurrentLocation()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchAndCenterOnCurrentLocation()
            } else {
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchAndCenterOnCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermissions()
            return
        }

        // Get the last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Center the map on the user's location
                centerMapOnLocation(location.longitude, location.latitude)

                // Add a marker at the user's location
                mapView.getMapboxMap().getStyle { style ->
                    val annotationApi = mapView.annotations
                    val pointAnnotationManager = annotationApi.createPointAnnotationManager()

                    // Create a point annotation
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(Point.fromLngLat(location.longitude, location.latitude))
                        .withIconImage("red_marker")

                    // Add the point annotation to the map
                    pointAnnotationManager.create(pointAnnotationOptions)
                }
            } else {
                Toast.makeText(this, "Unable to fetch location. Turn on GPS.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun centerMapOnLocation(longitude: Double, latitude: Double) {
        mapView.getMapboxMap().setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(longitude, latitude))
                .zoom(12.0) // Adjust zoom level for clarity
                .build()
        )
    }
    object BitmapUtils {
        fun getBitmapFromDrawable(drawable: Drawable): Bitmap? {
            return if (drawable is BitmapDrawable) {
                drawable.bitmap
            } else {
                val bitmap = Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
        }
    }
}
