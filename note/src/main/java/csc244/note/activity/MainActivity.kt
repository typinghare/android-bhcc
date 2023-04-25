package csc244.note.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import csc244.note.R

/**
 * https://docs.google.com/presentation/d/1vhXbZUPC9heZNvBlZwh7_QUijjSc5iEDXh28HgriC60/
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}