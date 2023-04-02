package csc244.internet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class PictureDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_display)

        val imageView: ImageView = findViewById(R.id.image_picture)
        val textAcknowledgments: TextView = findViewById(R.id.text_acknowledgments)

        val dogImageUrl = intent.getStringExtra(MainActivity.INTENT_DOG_IMAGE_URL)
        val catImageUrl = intent.getStringExtra(MainActivity.INTENT_CAT_IMAGE_URL)

        if (dogImageUrl != null) {
            ("API - " + MainActivity.API_DOT_IMAGE).also { textAcknowledgments.text = it }
            Glide.with(this).load(dogImageUrl).into(imageView)
        } else if (catImageUrl != null) {
            ("API - " + MainActivity.API_CAT_IMAGE).also { textAcknowledgments.text = it }
            Glide.with(this).load(catImageUrl).into(imageView)
        } else {
            closeActivity()
        }

        val btnBack: Button = findViewById(R.id.btn_back)
        btnBack.setOnClickListener { closeActivity() }
    }

    private fun closeActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }
}