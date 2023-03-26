package csc244.internet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textMainCaption: TextView = findViewById(R.id.text_main_caption)

        "Select a button to Click!".also { textMainCaption.text = it }
    }
}