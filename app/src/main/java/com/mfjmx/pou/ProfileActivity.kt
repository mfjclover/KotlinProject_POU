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

class ProfileActivity : BaseActivity() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            uploadImageToFirebaseStorage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())

        val backTextView = findViewById<TextView>(R.id.back_profile)
        val profileImageView = findViewById<ImageView>(R.id.profile_image)
        val profileNameTextView = findViewById<TextView>(R.id.profile_name)
        val currentUser = firebaseAuth.currentUser

        profileNameTextView.text = currentUser?.displayName ?: "Nombre de Usuario"
        profileImageView.setOnClickListener {
            pickImage.launch("image/*")
        }

        // Cargar imagen de perfil desde Firebase Storage o imagen predeterminada
        loadProfileImage()

        backTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_profile
    }

    private fun loadProfileImage() {
        val currentUser = firebaseAuth.currentUser
        val storageRef = FirebaseStorage.getInstance().reference
        val profileImageView = findViewById<ImageView>(R.id.profile_image)

        if (currentUser != null) {
            val profileImageRef = storageRef.child("ProfileImages/${currentUser.uid}/profile.jpg")

            profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                Glide.with(this).load(uri).into(profileImageView)
            }.addOnFailureListener {
                // Si falla, cargar imagen predeterminada desde Firebase Storage
                loadDefaultProfileImage()
            }
        } else {
            loadDefaultProfileImage()
        }
    }

    private fun loadDefaultProfileImage() {
        val storageRef = FirebaseStorage.getInstance().reference
        val defaultProfileImageRef = storageRef.child("ProfileImages/IconoPredeterminado.png")
        val profileImageView = findViewById<ImageView>(R.id.profile_image)

        defaultProfileImageRef.downloadUrl.addOnSuccessListener { uri ->
            Glide.with(this).load(uri).into(profileImageView)
        }.addOnFailureListener {
            Log.e("ProfileActivity", "Error al cargar la imagen predeterminada")
            showToastError("Error al cargar la imagen predeterminada")
        }
    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val user = firebaseAuth.currentUser
        if (user != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val profileImageRef = storageRef.child("ProfileImages/${user.uid}/profile.jpg")

            profileImageRef.putFile(imageUri)
                .addOnSuccessListener {
                    // Recargar la imagen de perfil después de la subida exitosa
                    loadProfileImage()
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