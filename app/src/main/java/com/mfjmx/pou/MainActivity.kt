package com.mfjmx.pou

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import android.util.Log
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView



class MainActivity : ComponentActivity() {
    private val TAG = "btaMainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d(TAG, "onCreate: The activity is being created.");

        val upmTitle: TextView = findViewById(R.id.university_upm)
        upmTitle.setOnClickListener {
            val intentUPM = Intent(this, UPMActivity::class.java)
            startActivity(intentUPM)
        }

        // ButtomNavigationMenu
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_map -> {
                    true
                }
                R.id.navigation_profile -> {
                    val intent = Intent(this, UPMActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }




        /*
        val buttonNext: Button = findViewById(R.id.mainButton)
        buttonNext.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)

        }*/
    }
}