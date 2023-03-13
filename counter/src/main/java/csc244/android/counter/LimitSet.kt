package csc244.android.counter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class LimitSet : AppCompatActivity() {
    companion object {
        val LIMIT = LimitSet::class.qualifiedName + ".limit"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.limit_set)

        val limit = intent.getIntExtra(LIMIT, 0)
        val limitInput = findViewById<TextView>(R.id.limit_input)

        val setButton = findViewById<Button>(R.id.limit_set_button)
        setButton.setOnClickListener {
            val intent = Intent(this, Main::class.java).apply {
                Log.d("limit", limitInput.text.toString());
                putExtra(LIMIT, limitInput.text.toString().toInt())
            }

            startActivity(intent)
        }
    }
}