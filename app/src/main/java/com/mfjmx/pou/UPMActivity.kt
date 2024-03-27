package com.mfjmx.pou

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity

class UPMActivity : ComponentActivity() {
    private val TAG = "btaUPMActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upm)
        Log.d(TAG, "onCreate: The upm activity is being created.");
    }
}
