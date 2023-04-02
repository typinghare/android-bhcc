package csc244.internet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class JokeDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joke_display)

        val setup = intent.getStringExtra(MainActivity.INTENT_JOKE_SETUP)
        val punchline = intent.getStringExtra(MainActivity.INTENT_JOKE_PUNCHLINE)

        val textSetup: TextView = findViewById(R.id.text_setup)
        val textPunchline: TextView = findViewById(R.id.text_punchline)

        setup.also { textSetup.text = it }
        punchline.also { textPunchline.text = it }

        val btnBack: Button = findViewById(R.id.btn_back)
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}