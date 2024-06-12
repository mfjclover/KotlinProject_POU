package com.mfjmx.pou

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : ComponentActivity(), LocationListener {

    private lateinit var locationManager: LocationManager
    private val locationPermissionCode = 2
    var latestLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        checkLocationPermissions()
    }

    override fun onResume() {
        super.onResume()
        setupBottomNavigationView()
    }

    protected abstract fun getLayoutResourceId(): Int

    private fun setupBottomNavigationView() {
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        when (this) {
            is MainActivity -> navView.selectedItemId = R.id.navigation_home
            is OpenStreetMapActivity -> navView.selectedItemId = R.id.navigation_map
            is ProfileActivity -> navView.selectedItemId = R.id.navigation_profile
        }

        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    if (this !is MainActivity) {
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        Toast.makeText(this, "Already on Home", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.navigation_map -> {
                    if (this !is OpenStreetMapActivity) {
                        if (latestLocation != null) {
                            val intent = Intent(this, OpenStreetMapActivity::class.java)
                            val bundle = Bundle()
                            bundle.putParcelable("location", latestLocation)
                            intent.putExtra("locationBundle", bundle)
                            startActivity(intent)
                        } else {
                            Log.e("BaseActivity", "Location not set yet.")
                            Toast.makeText(this, "Location not set yet", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Already on Map", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.navigation_profile -> {
                    if (this !is ProfileActivity) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    } else {
                        Toast.makeText(this, "Already on Profile", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> {
                    Toast.makeText(this, "Error: Unknown option selected", Toast.LENGTH_SHORT).show()
                    false
                }
            }
        }
    }

    private fun checkLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                locationPermissionCode
            )
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationPermissions()
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        latestLocation = location
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onProviderDisabled(provider: String) {}
}
