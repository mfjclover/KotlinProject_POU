package com.mfjmx.pou

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

abstract class BaseActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
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
                        val latestLocation = intent.getParcelableExtra<Location>("latestLocation")
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
}