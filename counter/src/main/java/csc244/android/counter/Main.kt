package csc244.android.counter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView

class Main : AppCompatActivity() {
    private var counterNumber = 0

    private var limit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        limit = intent.getIntExtra(LimitSet.LIMIT, 5)
        Log.d("limit, main", limit.toString())

        val minusButton = findViewById<Button>(R.id.minus_button)
        val plusButton = findViewById<Button>(R.id.plus_button)
        val resetButton = findViewById<Button>(R.id.reset_button)

        minusButton.setOnClickListener {
            if (counterNumber == limit && limit < 0) {
                renderLimitSetActivity()
            } else {
                counterNumber--
                displayNumber()
            }
        }

        plusButton.setOnClickListener {
            if (counterNumber == limit && limit > 0) {
                renderLimitSetActivity()
            } else {
                counterNumber++
                displayNumber()
            }
        }

        resetButton.setOnClickListener {
            counterNumber = 0
            displayNumber()
        }
    }

    private fun renderLimitSetActivity() {
        val intent = Intent(this, LimitSet::class.java).apply {
            putExtra(LimitSet.LIMIT, limit)
        }
        startActivity(intent)
    }

    private fun displayNumber() {
        val counterText = findViewById<TextView>(R.id.counter_number_display_text)
        counterText.text = counterNumber.toString()
    }
}