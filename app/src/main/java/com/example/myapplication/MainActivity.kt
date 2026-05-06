package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MainActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private var myLocationOverlay: MyLocationNewOverlay? = null

    // Markerlar uchun ma'lumotlar
    private val markerItems = listOf(
        MarkerData("Universitet", "Toshkent shahar, Yunusobod tumani, Amir Temur ko'chasi, 108-uy", 41.311081, 69.279737),
        MarkerData("Talabaning uyi", "Toshkent shahar, Chilonzor tumani, Lutfiy ko'chasi, 9-kvartal", 41.320000, 69.285000),
        MarkerData("Xiyobon", "Toshkent shahar, Mirobod tumani, Amir Temur xiyoboni", 41.315000, 69.282000)
    )

    private var selectionPoint1: GeoPoint? = null
    private var selectionMarker1: Marker? = null
    private var selectionMarker2: Marker? = null

    data class MarkerData(val title: String, val address: String, val lat: Double, val lon: Double)

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            enableMyLocationOverlay()
        } else {
            Toast.makeText(this, "Joylashuv ruxsati berilmadi", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this))
        setContentView(R.layout.activity_main)

        mapView = findViewById(R.id.mapview)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        val startPoint = GeoPoint(41.311081, 69.279737)
        mapView.controller.setZoom(14.0)
        mapView.controller.setCenter(startPoint)

        addMarkers()
        setupMapEvents()

        val btnMyLocation = findViewById<Button>(R.id.btn_my_location)
        btnMyLocation.setOnClickListener {
            val location = myLocationOverlay?.myLocation
            if (location != null) {
                mapView.controller.animateTo(location)
                mapView.controller.setZoom(17.0)
            } else {
                Toast.makeText(this, "Joylashuv aniqlanmoqda...", Toast.LENGTH_SHORT).show()
            }
        }

        checkPermissionsAndEnableLocation()
    }

    private fun addMarkers() {
        for (item in markerItems) {
            val marker = Marker(mapView)
            marker.position = GeoPoint(item.lat, item.lon)
            marker.title = item.title
            marker.snippet = item.address
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            
            marker.setOnMarkerClickListener { m, _ ->
                val myLoc = myLocationOverlay?.myLocation
                val distanceStr = if (myLoc != null) {
                    val dist = distanceBetween(myLoc.latitude, myLoc.longitude, m.position.latitude, m.position.longitude)
                    String.format("\nMasofa: %.2f km", dist / 1000)
                } else ""
                
                val fullInfo = "${item.title}\nManzil: ${item.address}$distanceStr"
                
                // 1. Snippetni yangilash va darchani ochish
                m.snippet = "Manzil: ${item.address}$distanceStr"
                m.showInfoWindow()
                
                // 2. Ekran pastida to'liq ma'lumotni ko'rsatish (Toast)
                Toast.makeText(this@MainActivity, fullInfo, Toast.LENGTH_LONG).show()
                
                mapView.controller.animateTo(m.position)
                true
            }
            
            mapView.overlays.add(marker)
        }
        mapView.invalidate()
    }

    private fun setupMapEvents() {
        val eventsOverlay = org.osmdroid.views.overlay.MapEventsOverlay(object : org.osmdroid.events.MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: org.osmdroid.util.GeoPoint?): Boolean {
                return true
            }

            override fun longPressHelper(p: org.osmdroid.util.GeoPoint?): Boolean {
                if (p == null) return false
                
                if (selectionPoint1 == null) {
                    selectionPoint1 = p
                    if (selectionMarker1 != null) mapView.overlays.remove(selectionMarker1)
                    if (selectionMarker2 != null) mapView.overlays.remove(selectionMarker2)
                    
                    selectionMarker1 = org.osmdroid.views.overlay.Marker(mapView)
                    selectionMarker1?.position = p
                    selectionMarker1?.title = "1-nuqta"
                    mapView.overlays.add(selectionMarker1)
                    
                    Toast.makeText(this@MainActivity, "1-nuqta tanlandi. 2-sini tanlang", Toast.LENGTH_SHORT).show()
                } else {
                    val dist = distanceBetween(selectionPoint1!!.latitude, selectionPoint1!!.longitude, p.latitude, p.longitude)
                    val distKm = String.format("%.2f", dist / 1000)
                    
                    selectionMarker2 = org.osmdroid.views.overlay.Marker(mapView)
                    selectionMarker2?.position = p
                    selectionMarker2?.title = "2-nuqta"
                    selectionMarker2?.snippet = "Masofa: $distKm km"
                    mapView.overlays.add(selectionMarker2)
                    selectionMarker2?.showInfoWindow()

                    Toast.makeText(this@MainActivity, "Masofa: $distKm km", Toast.LENGTH_LONG).show()
                    selectionPoint1 = null
                }
                mapView.invalidate()
                return true
            }
        })
        mapView.overlays.add(0, eventsOverlay)
    }

    private fun distanceBetween(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val radius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return radius * c
    }

    private fun checkPermissionsAndEnableLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            enableMyLocationOverlay()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun enableMyLocationOverlay() {
        val provider = GpsMyLocationProvider(this)
        myLocationOverlay = MyLocationNewOverlay(provider, mapView)
        myLocationOverlay?.enableMyLocation()
        myLocationOverlay?.enableFollowLocation() // Avtomatik kuzatish (ixtiyoriy)
        mapView.overlays.add(myLocationOverlay)
        mapView.invalidate()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
        myLocationOverlay?.enableMyLocation()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
        myLocationOverlay?.disableMyLocation()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDetach()
    }
}