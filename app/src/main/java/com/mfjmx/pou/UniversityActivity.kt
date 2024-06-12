package com.mfjmx.pou

import HotPoint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class UniversityActivity : BaseActivity() {
    private val TAG = "mfjmxUniversityActivity"
    private lateinit var textViewMap: Map<String, TextView>
    private lateinit var imageViewMap: Map<String, ImageView>
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())
        Log.d(TAG, "onCreate: The university activity is being created.")

        val universityId = intent.getStringExtra("university_id") ?: "UPM"

        val universityTitle = findViewById<TextView>(R.id.upm_title)
        universityTitle.text = universityId

        // Obtener una instancia y referencia de Firebase Database
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("hotPoints/$universityId")

        // Obtener una instancia de Firebase Storage
        storage = FirebaseStorage.getInstance()

        // Inicializar el mapa de TextViews e ImageViews
        textViewMap = mapOf(
            "hp1" to findViewById(R.id.text_hp_1),
            "hp2" to findViewById(R.id.text_hp_2),
            "hp3" to findViewById(R.id.text_hp_3),
            "hp4" to findViewById(R.id.text_hp_4),
            "hp5" to findViewById(R.id.text_hp_5),
            "hp6" to findViewById(R.id.text_hp_6)
        )

        imageViewMap = mapOf(
            "hp1" to findViewById(R.id.image_hp_1),
            "hp2" to findViewById(R.id.image_hp_2),
            "hp3" to findViewById(R.id.image_hp_3),
            "hp4" to findViewById(R.id.image_hp_4),
            "hp5" to findViewById(R.id.image_hp_5),
            "hp6" to findViewById(R.id.image_hp_6)
        )

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.children.forEach { hotPointSnapshot ->
                    val hotPoint = hotPointSnapshot.getValue(HotPoint::class.java)
                    // Esto asume que las claves en Firebase son "hp1", "hp2", etc.
                    val key = hotPointSnapshot.key
                    textViewMap[key]?.text = hotPoint?.name

                    val imageRef = storage.reference.child("HotPoints/$universityId/image_${key}.jpg")
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        val imageView = imageViewMap[key]
                        imageView?.let {
                            Glide.with(this@UniversityActivity)
                                .load(uri)
                                .into(it)
                        }
                        imageView?.setOnClickListener {
                            val intent = Intent(this@UniversityActivity, HotPointDetailActivity::class.java).apply {
                                putExtra("hotPointName", hotPoint?.name)
                                putExtra("hotPointDescription", hotPoint?.description)
                                putExtra("hotPointImageUrl", uri.toString())
                                putExtra("university_id", universityId)
                            }
                            startActivity(intent)
                        }
                    }.addOnFailureListener {
                        Log.w(TAG, "Error al cargar imagen para $key", it)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })

        val backButton = findViewById<TextView>(R.id.back_upm)
        backButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        val goToMapButton = findViewById<Button>(R.id.go_to_map)
        goToMapButton.setOnClickListener {
            val intent = Intent(this, OpenStreetMapActivity::class.java)
            intent.putExtra("university_id", universityId)
            startActivity(intent)
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_university
    }
}
