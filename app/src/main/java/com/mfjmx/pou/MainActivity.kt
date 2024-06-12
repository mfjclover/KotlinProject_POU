package com.mfjmx.pou

import android.content.Intent
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity() {
    private val TAG = "mfjmxMainActivity"

    // companion is the same than an static object in java
    companion object {
        private const val RC_SIGN_IN = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        Log.d(TAG, "onCreate: The activity is being created.")

        val upmTitle: TextView = findViewById(R.id.university_upm)
        upmTitle.setOnClickListener {
            val intentUPM = Intent(this, UniversityActivity::class.java)
            intentUPM.putExtra("university_id", "UPM")
            startActivity(intentUPM)
        }

        val uc3mTitle: TextView = findViewById(R.id.university_uc3m)
        uc3mTitle.setOnClickListener {
            val intentUC3M = Intent(this, UniversityActivity::class.java)
            intentUC3M.putExtra("university_id", "UC3M")
            startActivity(intentUC3M)
        }

        // Init authentication flow
        launchSignInFlow()
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_main
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                // user login succeeded
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this, R.string.signed_in, Toast.LENGTH_SHORT).show()
                Log.i(TAG, "onActivityResult " + getString(R.string.signed_in) + " " + user)
            } else {
                // user login failed
                Log.e(TAG, "Error starting auth session: ${response?.error?.errorCode}")
                Toast.makeText(this, R.string.signed_cancelled, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun launchSignInFlow() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
            )
            startActivityForResult(
                AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build(),
                RC_SIGN_IN
            )
        } else {
            updateUIWithUsername()
            Log.i(TAG, "El usuario ya está autenticado.")
        }
    }

    private fun updateUIWithUsername() {
        val user = FirebaseAuth.getInstance().currentUser
        val userNameTextView: TextView = findViewById(R.id.userNameTextView)
        user?.let {
            val name = user.displayName ?: "No Name"
            userNameTextView.text = "\uD83E\uDD35\u200D♂\uFE0F " + name
        }
    }

    override fun onResume() {
        super.onResume()
        updateUIWithUsername()
    }
}
