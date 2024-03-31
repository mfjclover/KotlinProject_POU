package com.mfjmx.pou

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class ProfileActivity : ComponentActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImageToFirebaseStorage(it)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val backTextView = findViewById<TextView>(R.id.back_profile)
        val profileImageView = findViewById<ImageView>(R.id.profile_image)
        val profileNameTextView = findViewById<TextView>(R.id.profile_name)
        val currentUser = firebaseAuth.currentUser

        profileNameTextView.text = currentUser?.displayName ?: "Nombre de Usuario"
        profileImageView.setOnClickListener {
            pickImage.launch("image/*")
        }

        backTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val username = user.displayName ?: "default"
            val storageRef = FirebaseStorage.getInstance().reference
            val profileImageRef = storageRef.child("ProfileImages/${username}.jpg")

            profileImageRef.putFile(imageUri)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata?.reference?.downloadUrl?.addOnSuccessListener { uri ->
                        val profileImageView = findViewById<ImageView>(R.id.profile_image)
                        Glide.with(this@ProfileActivity).load(uri).into(profileImageView)
                    }
                }
                .addOnFailureListener {
                    Log.e("ProfileActivity", "Error al subir la imagen")
                    showToastError("Error al subir la imagen")
                }
        }
    }
    private fun showToastError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

}


