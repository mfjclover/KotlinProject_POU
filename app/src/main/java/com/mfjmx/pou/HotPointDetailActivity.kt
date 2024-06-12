package com.mfjmx.pou

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class HotPointDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResourceId())

        val hotPointName = intent.getStringExtra("hotPointName")
        val hotPointDescription = intent.getStringExtra("hotPointDescription")
        val hotPointImageUrl = intent.getStringExtra("hotPointImageUrl")
        val nameTextView = findViewById<TextView>(R.id.hotpoint_detail_name)
        val descriptionTextView = findViewById<TextView>(R.id.hotpoint_detail_description)
        val imageView = findViewById<ImageView>(R.id.hotpoint_detail_image)

        nameTextView.text = hotPointName
        descriptionTextView.text = hotPointDescription

        Glide.with(this)
            .load(hotPointImageUrl)
            .override(com.bumptech.glide.request.target.Target.SIZE_ORIGINAL)
            .into(imageView)

        val backButton = findViewById<TextView>(R.id.back_hotpoint)
        backButton.setOnClickListener {
            startActivity(Intent(this, UPMActivity::class.java))
        }
    }

    override fun getLayoutResourceId(): Int {
        return R.layout.activity_hot_point_detail
    }
}